package com.lee.playandroid.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
class TodoViewModel(private val requestStatus: Int) : CoroutineViewModel() {

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

    class CreateFactory(private val requestStatus: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Int::class.java).newInstance(requestStatus)
        }
    }

    init {
        requestTodoData(LoadStatus.INIT)
    }

}