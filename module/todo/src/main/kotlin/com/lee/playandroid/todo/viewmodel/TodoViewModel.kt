package com.lee.playandroid.todo.viewmodel

import com.lee.playandroid.base.tools.PreferencesTools
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.entity.TodoType
import kotlinx.coroutines.flow.update

/**
 * todo页面 viewModel
 * @author jv.lee
 * @date 2022/5/19
 */
class TodoViewModel : BaseMVIViewModel<TodoViewState, IViewEvent, TodoViewIntent>() {

    private val accountService: AccountService = ModuleService.find()

    private val typeSavedKey = SP_KEY_TODO_TYPE.plus(accountService.getUserId())

    init {
        changePageData()
    }

    override fun initViewState() = TodoViewState()

    override fun dispatch(intent: TodoViewIntent) {
        when (intent) {
            is TodoViewIntent.ChangeTypeSelected -> {
                changePageData(intent.type)
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
) : IViewState

sealed class TodoViewIntent : IViewIntent {
    data class ChangeTypeSelected(@TodoType val type: Int) : TodoViewIntent()
}