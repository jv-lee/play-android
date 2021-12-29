package com.lee.playandroid.todo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.ItemTodoBinding
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_UPCOMING

/**
 * @author jv.lee
 * @date 2021/12/27
 * @description TODO列表数据适配器
 */
class TodoListAdapter(context: Context, private val status: Int, data: List<TodoData>) :
    ViewBindingAdapter<TodoData>(context, data) {

    init {
        addItemChildIds(R.id.const_container, R.id.btn_done, R.id.btn_delete)
        addItemStyles(TodoItem())
    }

    inner class TodoItem : ViewBindingItem<TodoData>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemTodoBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: TodoData, position: Int) {
            holder.getViewBinding<ItemTodoBinding>().apply {
                tvTitle.text = entity.title
                tvContent.text = entity.content
                btnDone.text = if (status == ARG_STATUS_UPCOMING)
                    root.context.getString(R.string.todo_item_complete)
                else
                    root.context.getString(R.string.todo_item_upcoming)
            }
        }

    }

}