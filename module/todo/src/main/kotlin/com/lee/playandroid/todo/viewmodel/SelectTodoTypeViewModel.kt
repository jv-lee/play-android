package com.lee.playandroid.todo.viewmodel

import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.vm.CoroutineViewModel
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.model.entity.TodoTypeData
import com.lee.playandroid.todo.model.entity.TodoTypeWheelData
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2022/1/2
 * @description
 */
class SelectTodoTypeViewModel : CoroutineViewModel() {

    private val _todoTypesLive = UiStateMutableLiveData()
    val todoTypes: UiStateLiveData = _todoTypesLive

    private fun requestTodoTypes() {
        launchIO {
            stateFlow {
                val type = PreferencesTools.get(SP_KEY_TODO_TYPE, TodoType.DEFAULT)
                val data = TodoTypeData.getTodoTypes()
                TodoTypeWheelData(type, data)
            }.collect {
                _todoTypesLive.postValue(it)
            }
        }
    }

    init {
        requestTodoTypes()
    }

}