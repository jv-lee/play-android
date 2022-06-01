package com.lee.playandroid.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.tools.PreferencesTools
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.model.entity.TodoTypeData
import com.lee.playandroid.todo.model.entity.TodoTypeWheelData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *
 * @author jv.lee
 * @date 2022/1/2
 */
class SelectTodoTypeViewModel : ViewModel() {

    private val accountService: AccountService = ModuleService.find()

    private val typeSavedKey = SP_KEY_TODO_TYPE.plus(accountService.getUserId())

    private val _viewStates = MutableStateFlow(SelectTodoTypeViewState())
    val viewStates: StateFlow<SelectTodoTypeViewState> = _viewStates

    init {
        requestTodoTypes()
    }

    private fun requestTodoTypes() {
        viewModelScope.launch {
            flow {
                val type = PreferencesTools.get(typeSavedKey, TodoType.DEFAULT)
                val data = TodoTypeData.getTodoTypes()
                emit(TodoTypeWheelData(type, data))
            }.collect { data ->
                _viewStates.update { it.copy(todoTypeWheelData = data) }
            }
        }
    }

}

data class SelectTodoTypeViewState(val todoTypeWheelData: TodoTypeWheelData = TodoTypeWheelData())