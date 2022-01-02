package com.lee.playandroid.todo.viewmodel

import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.todo.model.entity.TodoTypeData
import com.lee.playandroid.todo.model.entity.TodoTypeWheelData
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2022/1/2
 * @description
 */
class TodoViewModel : CoroutineViewModel() {

    private val _todoTypesLive = UiStateMutableLiveData()
    val todoTypes: UiStateLiveData = _todoTypesLive

    private fun requestTodoTypes() {
        launchIO {
            stateFlow {
                val data = TodoTypeData.getTodoTypes()
                TodoTypeWheelData(0, data)
            }.collect {
                _todoTypesLive.postValue(it)
            }
        }
    }

    init {
        requestTodoTypes()
    }

}