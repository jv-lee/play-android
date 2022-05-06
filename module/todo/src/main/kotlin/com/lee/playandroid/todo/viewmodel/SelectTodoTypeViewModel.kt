package com.lee.playandroid.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.model.entity.TodoTypeData
import com.lee.playandroid.todo.model.entity.TodoTypeWheelData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/1/2
 * @description
 */
class SelectTodoTypeViewModel : ViewModel() {

    private val _viewStates = MutableStateFlow(SelectTodoTypeViewState())
    val viewStates: StateFlow<SelectTodoTypeViewState> = _viewStates

    init {
        requestTodoTypes()
    }

    private fun requestTodoTypes() {
        viewModelScope.launch {
            flow {
                val type = PreferencesTools.get(SP_KEY_TODO_TYPE, TodoType.DEFAULT)
                val data = TodoTypeData.getTodoTypes()
                emit(TodoTypeWheelData(type, data))
            }.collect { data ->
                _viewStates.update { it.copy(todoTypeWheelData = data) }
            }
        }
    }

}

data class SelectTodoTypeViewState(val todoTypeWheelData: TodoTypeWheelData = TodoTypeWheelData())