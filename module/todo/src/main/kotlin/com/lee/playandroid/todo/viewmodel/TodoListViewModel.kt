package com.lee.playandroid.todo.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.extensions.putPageCache
import com.lee.library.tools.PreferencesTools
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.todo.constants.Constants.CACHE_KEY_TODO_CONTENT
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.api.ApiService
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_PARAMS_STATUS
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_COMPLETE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_UPCOMING
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author jv.lee
 * @date 2021/12/27
 * @description TodoViewModel TodoList页面使用，删改查处理
 */
class TodoListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    // 请求状态 1-完成；0未完成; 默认全部展示
    private val requestStatus = handle[ARG_PARAMS_STATUS] ?: 0

    // 请求类型
    private var requestType = PreferencesTools.get(SP_KEY_TODO_TYPE, TodoType.DEFAULT)

    // 缓存key 根据todo状态、todo类型、用户id
    private var cacheKey = CACHE_KEY_TODO_CONTENT.plus(requestStatus).plus(requestType)
        .plus(accountService.getUserId())

    private val deleteLock = AtomicBoolean(false)
    private val updateLock = AtomicBoolean(false)

    private val _viewEvents = Channel<TodoListViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    private val _todoDataFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(1))
    val todoDataFlow: UiStatePageStateFlow = _todoDataFlow

    init {
        dispatch(TodoListViewAction.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(action: TodoListViewAction) {
        when (action) {
            is TodoListViewAction.RequestPage -> {
                requestTodoData(action.status)
            }
            is TodoListViewAction.RequestDelete -> {
                requestDeleteTodo(action.position)
            }
            is TodoListViewAction.RequestUpdate -> {
                requestUpdateTodoStatus(action.position)
            }
            is TodoListViewAction.CheckResetRequestType -> {
                checkResetRequestType(action.type)
            }
        }
    }

    private fun requestTodoData(@LoadStatus status: Int) {
        viewModelScope.launch {
            _todoDataFlow.pageLaunch(status, { page ->
                applyData {
                    api.postTodoDataAsync(page, requestStatus, requestType)
                        .checkData()
                }
            }, {
                cacheManager.getCache(cacheKey)
            }, {
                cacheManager.putPageCache(cacheKey, it)
            })
        }
    }

    private fun requestDeleteTodo(position: Int) {
        if (deleteLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    val data = _todoDataFlow.getValueData<PageData<TodoData>>()!!
                    val item = data.data[position]

                    val response = api.postDeleteTodoAsync(item.id)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        removeCacheItem(item)
                        emit(item)
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.catch { error ->
                    deleteLock.set(false)
                    _viewEvents.send(TodoListViewEvent.ActionFailed(error = error))
                }.collect {
                    deleteLock.set(false)
                    _viewEvents.send(TodoListViewEvent.DeleteTodoActionSuccess(it))
                }
            }
        }
    }

    private fun requestUpdateTodoStatus(position: Int) {
        if (updateLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    val data = _todoDataFlow.getValueData<PageData<TodoData>>()!!
                    val item = data.data[position]

                    val newItem =
                        item.copy(status = if (requestStatus == ARG_STATUS_UPCOMING) ARG_STATUS_COMPLETE else ARG_STATUS_UPCOMING)
                    val response = api.postUpdateTodoStatusAsync(item.id, newItem.status)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        // 根据数据源删除item
                        removeCacheItem(item)
                        // 返回copy数据源，因为返回的数据源要根据新的status做数据拉取处理
                        emit(newItem)
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.catch { error ->
                    updateLock.set(false)
                    _viewEvents.send(TodoListViewEvent.ActionFailed(error = error))
                }.collect { data ->
                    updateLock.set(false)
                    _viewEvents.send(TodoListViewEvent.UpdateTodoActionSuccess(todo = data))
                }
            }
        }
    }

    /**
     * 切换todo类型
     */
    private fun checkResetRequestType(@TodoType type: Int) {
        viewModelScope.launch {
            if (requestType != type) {
                PreferencesTools.put(SP_KEY_TODO_TYPE, type)
                requestType = type
                cacheKey = CACHE_KEY_TODO_CONTENT.plus(requestStatus).plus(requestType)
                    .plus(accountService.getUserId())
                _viewEvents.send(TodoListViewEvent.ResetRequestType)
            }
        }
    }

    private fun removeCacheItem(todo: TodoData) {
        // 内存移除
        _todoDataFlow.getValueData<PageData<TodoData>>()?.apply {
            this.data.remove(todo)
        }

        // 缓存移除
        cacheManager.getCache<PageData<TodoData>>(cacheKey)
            ?.apply {
                if (data.remove(todo)) {
                    cacheManager.putCache(cacheKey, this)
                }
            }
    }

}

sealed class TodoListViewEvent {
    data class DeleteTodoActionSuccess(val todo: TodoData) : TodoListViewEvent()
    data class UpdateTodoActionSuccess(val todo: TodoData) : TodoListViewEvent()
    data class ActionFailed(val error: Throwable) : TodoListViewEvent()
    object ResetRequestType : TodoListViewEvent()
}

sealed class TodoListViewAction {
    data class RequestPage(@LoadStatus val status: Int) : TodoListViewAction()
    data class RequestDelete(val position: Int) : TodoListViewAction()
    data class RequestUpdate(val position: Int) : TodoListViewAction()
    data class CheckResetRequestType(@TodoType val type: Int) : TodoListViewAction()
}