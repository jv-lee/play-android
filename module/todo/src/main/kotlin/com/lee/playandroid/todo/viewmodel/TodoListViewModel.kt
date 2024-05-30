package com.lee.playandroid.todo.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.getCache
import com.lee.playandroid.base.extensions.putCache
import com.lee.playandroid.base.extensions.putPageCache
import com.lee.playandroid.base.tools.PreferencesTools
import com.lee.playandroid.base.utils.NetworkUtil
import com.lee.playandroid.base.uistate.*
import com.lee.playandroid.common.constants.ApiConstants
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.entity.TodoData
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.constants.Constants.CACHE_KEY_TODO_CONTENT
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.api.ApiService
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.ui.CreateTodoFragment
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_PARAMS_STATUS
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_COMPLETE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_UPCOMING
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * TodoViewModel TodoList页面使用，删改查处理
 * @author jv.lee
 * @date 2021/12/27
 */
class TodoListViewModel(handle: SavedStateHandle) : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    // 请求状态 1-完成；0未完成; 默认全部展示
    private val requestStatus = handle[ARG_PARAMS_STATUS] ?: 0

    // 保存type选择类型key
    private val typeSavedKey = SP_KEY_TODO_TYPE.plus(accountService.getUserId())

    // 请求类型
    private var requestType = PreferencesTools.get(typeSavedKey, TodoType.DEFAULT)

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
        dispatch(TodoListViewIntent.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(intent: TodoListViewIntent) {
        when (intent) {
            is TodoListViewIntent.RequestPage -> {
                requestTodoData(intent.status)
            }
            is TodoListViewIntent.RequestDelete -> {
                requestDeleteTodo(intent.position)
            }
            is TodoListViewIntent.RequestUpdate -> {
                requestUpdateTodoStatus(intent.position)
            }
            is TodoListViewIntent.CheckResetRequestType -> {
                checkResetRequestType(intent.type)
            }
            is TodoListViewIntent.NavigationEditTodoPage -> {
                navigationEditTodoPage(intent.todo)
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
                    check(NetworkUtil.isNetworkConnected(app)) {
                        app.getString(R.string.network_not_access)
                    }

                    val data = _todoDataFlow.getValueData<PageData<TodoData>>()!!
                    val item = data.data[position]

                    val response = api.postDeleteTodoAsync(item.id)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        removeCacheItem(item)
                        emit(item)
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.onStart {
                    _viewEvents.send(TodoListViewEvent.ResetSlidingState)
                }.catch { error ->
                    _viewEvents.send(TodoListViewEvent.ActionFailed(error = error))
                }.onCompletion {
                    deleteLock.set(false)
                }.collect { data ->
                    _viewEvents.send(
                        TodoListViewEvent.DeleteTodoActionSuccess(
                            position = position,
                            todo = data
                        )
                    )
                }
            }
        }
    }

    private fun requestUpdateTodoStatus(position: Int) {
        if (updateLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    check(NetworkUtil.isNetworkConnected(app)) {
                        app.getString(R.string.network_not_access)
                    }

                    val data = _todoDataFlow.getValueData<PageData<TodoData>>()!!
                    val item = data.data[position]

                    val newItem =
                        item.copy(
                            status = if (requestStatus == ARG_STATUS_UPCOMING) {
                                ARG_STATUS_COMPLETE
                            } else ARG_STATUS_UPCOMING
                        )
                    val response = api.postUpdateTodoStatusAsync(item.id, newItem.status)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        // 根据数据源删除item
                        removeCacheItem(item)
                        // 返回copy数据源，因为返回的数据源要根据新的status做数据拉取处理
                        emit(newItem)
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.onStart {
                    _viewEvents.send(TodoListViewEvent.ResetSlidingState)
                }.catch { error ->
                    _viewEvents.send(TodoListViewEvent.ActionFailed(error = error))
                }.onCompletion {
                    updateLock.set(false)
                }.collect { data ->
                    _viewEvents.send(
                        TodoListViewEvent.UpdateTodoActionSuccess(
                            position = position,
                            todo = data
                        )
                    )
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
                PreferencesTools.put(typeSavedKey, type)
                requestType = type
                cacheKey = CACHE_KEY_TODO_CONTENT.plus(requestStatus).plus(requestType)
                    .plus(accountService.getUserId())
                _viewEvents.send(TodoListViewEvent.ResetRequestType)
            }
        }
    }

    private fun navigationEditTodoPage(todo: TodoData) {
        viewModelScope.launch {
            val bundle = Bundle().apply {
                putInt(CreateTodoFragment.ARG_PARAMS_TYPE, CreateTodoFragment.ARG_TYPE_EDIT)
                putParcelable(CreateTodoFragment.ARG_PARAMS_TODO, todo)
            }
            _viewEvents.send(TodoListViewEvent.NavigationEditTodoPage(bundle))
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
    object ResetSlidingState : TodoListViewEvent()
    data class DeleteTodoActionSuccess(val position: Int, val todo: TodoData) : TodoListViewEvent()
    data class UpdateTodoActionSuccess(val position: Int, val todo: TodoData) : TodoListViewEvent()
    data class ActionFailed(val error: Throwable) : TodoListViewEvent()
    object ResetRequestType : TodoListViewEvent()
    data class NavigationEditTodoPage(val bundle: Bundle) : TodoListViewEvent()
}

sealed class TodoListViewIntent {
    data class RequestPage(@LoadStatus val status: Int) : TodoListViewIntent()
    data class RequestDelete(val position: Int) : TodoListViewIntent()
    data class RequestUpdate(val position: Int) : TodoListViewIntent()
    data class CheckResetRequestType(@TodoType val type: Int) : TodoListViewIntent()
    data class NavigationEditTodoPage(val todo: TodoData) : TodoListViewIntent()
}