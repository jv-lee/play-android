package com.lee.playandroid.todo.viewmodel

import com.lee.library.viewstate.UiStateLiveData
import com.lee.library.viewstate.UiStateMutableLiveData
import com.lee.library.viewstate.stateFlow
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.api.ApiService
import com.lee.playandroid.todo.model.entity.TodoType
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/12/28
 * @description 创建Todo列表ViewModel
 */
class CreateTodoViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()

    private val _todoLive = UiStateMutableLiveData()
    val todoLive: UiStateLiveData = _todoLive

    fun requestAddTodo(title: String, content: String, date: String, priority: Int) {
        launchIO {
            stateFlow {
                if (title.isEmpty() || content.isEmpty()) {
                    throw RuntimeException("title or content is not empty.")
                }
                val type = PreferencesTools.get(SP_KEY_TODO_TYPE, TodoType.DEFAULT)
                api.postAddTodoAsync(
                    title,
                    content,
                    date,
                    priority = priority,
                    type = type
                ).checkData()
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
                    api.postUpdateTodoAsync(
                        id, title, content, dateStr, type, priority, status
                    ).checkData()
                }.collect {
                    _todoLive.postValue(it)
                }
            }
        }
    }

}