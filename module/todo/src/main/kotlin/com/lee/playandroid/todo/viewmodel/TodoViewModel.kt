package com.lee.playandroid.todo.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.todo.model.repository.ApiRepository
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_PARAMS_STATUS

/**
 * @author jv.lee
 * @date 2021/12/27
 * @description TodoViewModel
 */
class TodoViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val requestStatus = handle[ARG_PARAMS_STATUS] ?: 0

    private val apiRepository = ApiRepository()

    val todoDataLive = UiStatePageLiveData()

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

    init {
        requestTodoData(LoadStatus.INIT)
    }

}