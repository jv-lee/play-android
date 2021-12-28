package com.lee.playandroid.todo.viewmodel

import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.todo.model.repository.ApiRepository

/**
 * @author jv.lee
 * @date 2021/12/28
 * @description 创建Todo列表ViewModel
 */
class CreateTodoViewModel : CoroutineViewModel() {

    private val apiRepository = ApiRepository()

    fun requestAddTodo(title: String, content: String, date: String, priority: Int) {
        launchIO {
            apiRepository.api.postAddTodoAsync(title, content, date, priority = priority)
                .checkData()
        }
    }

}