package com.lee.playandroid.todo.viewmodel

import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.todo.model.repository.ApiRepository

/**
 * @author jv.lee
 * @date 2021/12/27
 * @description
 */
class TodoViewModel : CoroutineViewModel() {

    private val apiRepository = ApiRepository()

    private val todoDataLive = UiStatePageLiveData()

    fun requestTodoData(@LoadStatus status: Int) {
        launchIO {
            todoDataLive.apply {
                pageLaunch(status, { page ->
                    applyData {
                        apiRepository.api.postTodoDataAsync(page, 1).checkData()
                    }
                })
            }
        }
    }

}