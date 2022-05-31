package com.lee.playandroid.todo.model.entity

import com.lee.library.base.ApplicationExtensions.app
import com.lee.playandroid.todo.R

/**
 *
 * @author jv.lee
 * @date 2022/1/2
 */
data class TodoTypeData(val type: Int, val name: String) {
    companion object {
        fun getTodoTypes(): MutableList<TodoTypeData> {
            return arrayListOf<TodoTypeData>().apply {
                add(TodoTypeData(TodoType.DEFAULT, app.getString(R.string.todo_title_default)))
                add(TodoTypeData(TodoType.WORK, app.getString(R.string.todo_title_work)))
                add(TodoTypeData(TodoType.LIFE, app.getString(R.string.todo_title_life)))
                add(TodoTypeData(TodoType.PLAY, app.getString(R.string.todo_title_play)))
            }
        }
    }
}

annotation class TodoType {
    companion object {
        const val DEFAULT = 0
        const val WORK = 1
        const val LIFE = 2
        const val PLAY = 3
    }
}

data class TodoTypeWheelData(
    val startIndex: Int = 0,
    val todoTypes: MutableList<TodoTypeData> = arrayListOf()
)