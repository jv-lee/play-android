package com.lee.playandroid.todo.ui.listener

import com.lee.playandroid.library.common.entity.TodoData

/**
 * @author jv.lee
 * @date 2021/12/29
 * @description
 */
interface TodoActionListener {
    fun addAction(todo: TodoData)
    fun updateAction(todo: TodoData)
    fun moveAction(todo: TodoData)
}