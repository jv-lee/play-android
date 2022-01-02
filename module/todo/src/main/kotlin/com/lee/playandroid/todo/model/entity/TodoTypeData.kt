package com.lee.playandroid.todo.model.entity

/**
 * @author jv.lee
 * @date 2022/1/2
 * @description
 */
data class TodoTypeData(val type: Int, val name: String) {
    companion object {
        fun getTodoTypes(): MutableList<TodoTypeData> {
            return arrayListOf<TodoTypeData>().apply {
                add(TodoTypeData(TodoType.DEFAULT, "只用这一个"))
                add(TodoTypeData(TodoType.WORK, "工作"))
                add(TodoTypeData(TodoType.LIFE, "生活"))
                add(TodoTypeData(TodoType.ENTERTAINMENT, "娱乐"))
            }
        }
    }
}

annotation class TodoType {
    companion object {
        const val DEFAULT = 0
        const val WORK = 1
        const val LIFE = 2
        const val ENTERTAINMENT = 3
    }
}

data class TodoTypeWheelData(val startIndex: Int, val todoTypes: MutableList<TodoTypeData>)