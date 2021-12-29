package com.lee.playandroid.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.todo.model.repository.ApiRepository
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
class TodoViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val requestStatus = handle[ARG_PARAMS_STATUS] ?: 0

    private val apiRepository = ApiRepository()

    private val deleteLock = AtomicBoolean(false)
    private val updateLock = AtomicBoolean(false)

    val todoDataLive = UiStatePageLiveData()

    private val _todoDeleteLive = MutableLiveData<UiState>()
    val todoDeleteLive: LiveData<UiState> = _todoDeleteLive

    private val _todoUpdateLive = MutableLiveData<UiState>()
    val todoUpdateLive: LiveData<UiState> = _todoUpdateLive

    fun requestTodoData(@LoadStatus status: Int) {
        launchIO {
            todoDataLive.apply {
                pageLaunch(status, { page ->
                    applyData {
                        apiRepository.api.postTodoDataAsync(page, requestStatus).checkData()
                    }
                })
            }
        }
    }

    fun requestDeleteTodo(position: Int) {
        if (deleteLock.get()) return
        deleteLock.set(true)

        launchIO {
            stateFlow {
                val data = todoDataLive.getValueData<PageData<TodoData>>()!!
                val item = data.data[position]

                val response = apiRepository.api.postDeleteTodoAsync(item.id)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    data.data.remove(item)
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

                item.status =
                    if (requestStatus == ARG_STATUS_UPCOMING) ARG_STATUS_COMPLETE else ARG_STATUS_UPCOMING
                val response = apiRepository.api.postUpdateTodoStatusAsync(item.id, item.status)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    data.data.remove(item)
                    item
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.collect {
                updateLock.set(false)
                _todoUpdateLive.postValue(it)
            }
        }
    }

    init {
        requestTodoData(LoadStatus.INIT)
    }

}