package com.lee.playandroid.base.widget.toolbar.menu

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.lee.playandroid.base.R
import com.lee.playandroid.base.extensions.dp2px
import com.lee.playandroid.base.widget.toolbar.TitleToolbar

/**
 * 自定义popupMenu弹窗帮助类
 * @author jv.lee
 * @date 2020/4/22
 */
class CustomPopupMenuHelper(var context: Context, var menuResId: Int) : View.OnClickListener {

    var toolbarClickListener: TitleToolbar.ClickListener? = null

    private val menuInflater by lazy { CustomMenuInflater(context) }

    private val rootView: View by lazy {
        createCardView().also { card ->
            card.addView(
                menuInflater.apply { inflate(menuResId) }.buildMenuView().also {
                    for (index in 0..it.childCount) {
                        it.getChildAt(index)?.setOnClickListener(this)
                    }
                }
            )
        }
    }

    val menuPW: PopupWindow by lazy {
        PopupWindow(
            rootView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ).apply {
            isFocusable = true
            setBackgroundDrawable(ColorDrawable())
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            menuPW.dismiss()
            toolbarClickListener?.menuItemClick(it)
        }
    }

    private fun createCardView() = CardView(context).apply {
        val cardRadius = context.dp2px(16)
        val contentPadding = context.dp2px(3).toInt()

        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        useCompatPadding = true
        radius = cardRadius
        setContentPadding(0, contentPadding, 0, contentPadding)
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.baseItemColor))
    }
}