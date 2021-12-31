package com.lee.playandroid.todo.viewmodel

import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.todo.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/12/28
 * @description 创建Todo列表ViewModel
 */
class CreateTodoViewModel : CoroutineViewModel() {

    private val apiRepository = ApiRepository()

    private val _todoLive = UiStateMutableLiveData()
    val todoLive: UiStateLiveData = _todoLive

    fun requestAddTodo(title: String, content: String, date: String, priority: Int) {
        launchIO {
            stateFlow {
                if (title.isEmpty() || content.isEmpty()) {
                    throw RuntimeException("title or content is not empty.")
                }
                apiRepository.api.postAddTodoAsync(title, content, date, priority = priority)
                    .checkData()
            }.collect {
                _todoLive.postValue(it)
            }
        }
    }

    fun requestUpdateTodo(todoData: TodoData) {
        launchIO {
            todoData.apply {
                stateFlow {
                    if (title.isEmpty() || content.isEmpty()) {
                        throw RuntimeException("title or content is not empty.")
                    }
                    apiRepository.api.postUpdateTodoAsync(
                        id, title, content, dateStr, type, priority, status
                    ).checkData()
                }.collect {
                    _todoLive.postValue(it)
                }
            }
        }
    }

}