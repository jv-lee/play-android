package com.lee.playandroid.todo.viewmodel

import androidx.lifecycle.ViewModel
import com.lee.playandroid.base.tools.PreferencesTools
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.entity.TodoType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 *
 * @author jv.lee
 * @date 2022/5/19
 */
class TodoViewModel : ViewModel() {

    private val accountService: AccountService = ModuleService.find()

    private val typeSavedKey = SP_KEY_TODO_TYPE.plus(accountService.getUserId())

    private val _viewStates = MutableStateFlow(TodoViewState())
    val viewStates: StateFlow<TodoViewState> = _viewStates

    init {
        changePageData()
    }

    fun dispatch(action: TodoViewAction) {
        when (action) {
            is TodoViewAction.ChangeTypeSelected -> {
                changePageData(action.type)
            }
        }
    }

    private fun changePageData(type: Int = PreferencesTools.get(typeSavedKey, TodoType.DEFAULT)) {
        val textResId = when (type) {
            TodoType.WORK -> R.string.todo_title_work
            TodoType.LIFE -> R.string.todo_title_life
            TodoType.PLAY -> R.string.todo_title_play
            else -> R.string.todo_title_default
        }
        _viewStates.update { it.copy(titleRes = textResId) }
    }

}

data class TodoViewState(
    val titleRes: Int = R.string.todo_title_default
)

sealed class TodoViewAction {
    data class ChangeTypeSelected(@TodoType val type: Int) : TodoViewAction()
}