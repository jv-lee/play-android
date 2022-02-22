package com.lee.playandroid.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.annotation.LoadStatus
import com.lee.library.mvvm.ui.*
import com.lee.library.mvvm.vm.CoroutineViewModel
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.todo.constants.Constants
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.api.ApiService
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_PARAMS_STATUS
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_COMPLETE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_UPCOMING
import kotlinx.coroutines.flow.collect
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author jv.lee
 * @date 2021/12/27
 * @description TodoViewModel TodoList页面使用，删改查处理
 */
class TodoListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val requestStatus = handle[ARG_PARAMS_STATUS] ?: 0

    private var requestType = PreferencesTools.get(SP_KEY_TODO_TYPE, TodoType.DEFAULT)

    private var cacheKey = Constants.CACHE_KEY_TODO_CONTENT + requestStatus + requestType

    private val cacheManager = CacheManager.getDefault()

    private val api = createApi<ApiService>()

    private val deleteLock = AtomicBoolean(false)
    private val updateLock = AtomicBoolean(false)

    private val _todoDeleteLive = UiStateMutableLiveData()
    val todoDeleteLive: UiStateLiveData = _todoDeleteLive

    private val _todoUpdateLive = UiStateMutableLiveData()
    val todoUpdateLive: UiStateLiveData = _todoUpdateLive

    private val _todoDataLive = MutableLiveData<UiStatePage>(UiStatePage.Default(1))
    val todoDataLive: LiveData<UiStatePage> = _todoDataLive

    fun requestTodoData(@LoadStatus status: Int) {
        launchIO {
            _todoDataLive.pageLaunch(status, { page ->
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

    fun requestDeleteTodo(position: Int) {
        if (deleteLock.get()) return
        deleteLock.set(true)

        launchIO {
            stateFlow {
                val data = todoDataLive.getValueData<PageData<TodoData>>()!!
                val item = data.data[position]

                val response = api.postDeleteTodoAsync(item.id)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    removeCacheItem(item)
                    position
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.collect {
                deleteLock.set(false)
                _todoDeleteLive.postValue(it)
            }
        }
    }

    fun requestUpdateTodoStatus(position: Int) {
        if (updateLock.get()) return
        updateLock.set(true)

        launchIO {
            stateFlow {
                val data = todoDataLive.getValueData<PageData<TodoData>>()!!
                val item = data.data[position]

                val newItem =
                    item.copy(status = if (requestStatus == ARG_STATUS_UPCOMING) ARG_STATUS_COMPLETE else ARG_STATUS_UPCOMING)
                val response = api.postUpdateTodoStatusAsync(item.id, newItem.status)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    // 根据数据源删除item
                    removeCacheItem(item)
                    // 返回copy数据源，因为返回的数据源要根据新的status做数据拉取处理
                    newItem
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.collect {
                updateLock.set(false)
                _todoUpdateLive.postValue(it)
            }
        }
    }

    private fun removeCacheItem(todo: TodoData) {
        // 内存移除
        todoDataLive.getValueData<PageData<TodoData>>()?.apply {
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

    fun checkResetRequestType(@TodoType type: Int): Boolean {
        if (requestType != type) {
            PreferencesTools.put(SP_KEY_TODO_TYPE, type)
            requestType = type
            cacheKey = Constants.CACHE_KEY_TODO_CONTENT + requestStatus + requestType
            return true
        }
        return false
    }

    init {
        requestTodoData(LoadStatus.INIT)
    }

}