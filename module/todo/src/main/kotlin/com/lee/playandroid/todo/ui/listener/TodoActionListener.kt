package com.lee.playandroid.todo.ui.listener

import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.todo.model.entity.TodoType

/**
 * todo双状态列表行为通信
 * @author jv.lee
 * @date 2021/12/29
 */
interface TodoActionListener {
    /**
     * 添加TODO数据 回调todo列表更新
     */
    fun addAction(todo: TodoData?)

    /**
     * 更改TODO数据 回调todo列表更新
     */
    fun updateAction(todo: TodoData?)

    /**
     * 更改TODO状态 回调todo列表更新
     */
    fun moveAction(todo: TodoData)

    /**
     * 更改TODO列表type 重新刷新数据 回到todo列表更新
     */
    fun notifyAction(@TodoType type: Int)
}