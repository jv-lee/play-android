package com.lee.playandroid.todo.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.common.entity.TodoData
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.todo.R
import com.lee.playandroid.base.tools.PreferencesTools
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.model.api.ApiService
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.ui.CreateTodoFragment.Companion.ARG_TYPE_CREATE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_SAVE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_UPDATE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_VALUE_TODO
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_UPCOMING
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建todo内容页面viewModel
 * @author jv.lee
 * @date 2022/4/8
 */
class CreateTodoViewModel(private val type: Int, private val todoData: TodoData?) : ViewModel() {

    private val api = createApi<ApiService>()

    private val accountService: AccountService = ModuleService.find()

    private val typeSavedKey = SP_KEY_TODO_TYPE.plus(accountService.getUserId())

    private val _viewStates = MutableStateFlow(
        CreateTodoViewState(type = PreferencesTools.get(typeSavedKey, TodoType.DEFAULT))
    )
    val viewStates: StateFlow<CreateTodoViewState> = _viewStates

    private val _viewEvents = Channel<CreateTodoViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        initPageState()
    }

    fun dispatch(intent: CreateTodoViewIntent) {
        when (intent) {
            is CreateTodoViewIntent.ChangeTitle -> {
                changeTitle(intent.title)
            }
            is CreateTodoViewIntent.ChangeContent -> {
                changeContent(intent.content)
            }
            is CreateTodoViewIntent.ChangePriority -> {
                changePriority(intent.priority)
            }
            is CreateTodoViewIntent.ChangeDate -> {
                changeDate(intent.date)
            }
            is CreateTodoViewIntent.RequestPostTodo -> {
                requestPostTodo()
            }
        }
    }

    private fun initPageState() {
        val dateStr = todoData?.dateStr ?: dateToStrFormat()
        _viewStates.update {
            it.copy(
                appTitleRes = if (todoData == null) R.string.title_create_todo
                else R.string.title_edit_todo,
                isCreate = todoData == null,
                title = todoData?.title ?: "",
                content = todoData?.content ?: "",
                priority = todoData?.priority ?: TodoData.PRIORITY_LOW,
                date = todoData?.dateStr ?: dateToStrFormat(),
                calendar = stringToCalendar(dateStr)
            )
        }
    }

    private fun changeTitle(title: String) {
        _viewStates.update { it.copy(title = title) }
    }

    private fun changeContent(content: String) {
        _viewStates.update { it.copy(content = content) }
    }

    private fun changePriority(priority: Int) {
        _viewStates.update { it.copy(priority = priority) }
    }

    private fun changeDate(date: String) {
        _viewStates.update { it.copy(date = date, calendar = stringToCalendar(date)) }
    }

    private fun requestPostTodo() {
        if (viewStates.value.isCreate) requestAddTodo() else requestUpdateTodo()
    }

    private fun requestAddTodo() {
        viewModelScope.launch {
            flow {
                val response = api.postAddTodoAsync(
                    viewStates.value.title,
                    viewStates.value.content,
                    viewStates.value.date,
                    viewStates.value.type,
                    viewStates.value.priority
                ).checkData()
                emit(response)
            }.onStart {
                _viewStates.update { it.copy(isLoading = true) }
            }.catch { error ->
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(CreateTodoViewEvent.RequestFailed(error))
            }.collect { data ->
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(todoDataToSuccessEvent(data))
            }
        }
    }

    private fun requestUpdateTodo() {
        viewModelScope.launch {
            flow {
                val response = api.postUpdateTodoAsync(
                    todoData?.id ?: 0,
                    viewStates.value.title,
                    viewStates.value.content,
                    viewStates.value.date,
                    viewStates.value.type,
                    viewStates.value.priority,
                    todoData?.status ?: ARG_STATUS_UPCOMING
                ).checkData()
                emit(response)
            }.onStart {
                _viewStates.update { it.copy(isLoading = true) }
            }.catch { error ->
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(CreateTodoViewEvent.RequestFailed(error))
            }.collect { data ->
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(todoDataToSuccessEvent(data))
            }
        }
    }

    private fun todoDataToSuccessEvent(todoData: TodoData): CreateTodoViewEvent.RequestSuccess {
        // 返回页面结果类型key 保存动作 ｜｜ 更新动作
        val resultKey = if (type == ARG_TYPE_CREATE) REQUEST_KEY_SAVE else REQUEST_KEY_UPDATE

        // 返回页面结果bundle数据 创建的todo还是更新的todo todo实体
        val bundle = Bundle().apply { putParcelable(REQUEST_VALUE_TODO, todoData) }

        // 处理成功后toast提示message
        val message = if (type == ARG_TYPE_CREATE) app.getString(R.string.todo_create_success)
        else app.getString(R.string.todo_update_success)

        return CreateTodoViewEvent.RequestSuccess(resultKey, message, bundle)
    }

    private fun stringToCalendar(dateStr: String): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr)!!
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    private fun dateToStrFormat(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    class CreateFactory(private val type: Int, private val todoData: TodoData?) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Int::class.java, TodoData::class.java)
                .newInstance(type, todoData)
        }
    }
}

data class CreateTodoViewState(
    var type: Int = TodoType.DEFAULT,
    val isLoading: Boolean = false,
    val appTitleRes: Int = R.string.title_create_todo,
    val isCreate: Boolean = true,
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val calendar: Calendar = Calendar.getInstance(),
    val priority: Int = TodoData.PRIORITY_LOW
)

sealed class CreateTodoViewEvent {
    data class RequestSuccess(
        val resultKey: String,
        val message: String,
        val bundle: Bundle
    ) : CreateTodoViewEvent()

    data class RequestFailed(val error: Throwable) : CreateTodoViewEvent()
}

sealed class CreateTodoViewIntent {
    data class ChangeTitle(val title: String) : CreateTodoViewIntent()
    data class ChangeContent(val content: String) : CreateTodoViewIntent()
    data class ChangePriority(val priority: Int) : CreateTodoViewIntent()
    data class ChangeDate(val date: String) : CreateTodoViewIntent()
    object RequestPostTodo : CreateTodoViewIntent()
}