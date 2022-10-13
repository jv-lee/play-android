package com.lee.playandroid.base.widget.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.lee.playandroid.base.R
import com.lee.playandroid.base.extensions.dp2px
import com.lee.playandroid.base.extensions.px2dp
import com.lee.playandroid.base.extensions.statusBarHeight

/**
 * 自定义toolbar容器
 * @author jv.lee
 * @date 2020/4/1
 */
open class CustomToolbarLayout : ConstraintLayout {

    private var statusBarHeight = context.statusBarHeight
    private var toolbarLayoutHeight = initLayoutHeight()

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributes: AttributeSet) : this(context, attributes, 0)
    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    ) {
        minHeight = toolbarLayoutHeight

        initStatusBarPadding()
        initBackground()
    }

    /**
     * 初始化布局高度
     */
    open fun initLayoutHeight(): Int {
        val toolbarHeight = resources.getDimension(R.dimen.toolbar_height).toInt()
        return toolbarHeight + statusBarHeight
    }

    /**
     * 设置默认背景色
     */
    open fun initBackground() {
        background ?: kotlin.run {
            setBackgroundColor(ContextCompat.getColor(context, R.color.baseItemColor))
        }
    }

    /**
     * 设置状态栏填充padding
     */
    open fun initStatusBarPadding() {
        setPadding(context.dp2px(16).toInt(), statusBarHeight, context.dp2px(16).toInt(), 0)
    }

    /**
     * 获取toolbarLayout高度
     */
    fun getToolbarLayoutHeight() = toolbarLayoutHeight

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight() = statusBarHeight

    fun initBottomLine() {
        val lineView = View(context)
        lineView.run {
            id = R.id.toolbar_line
            layoutParams = LayoutParams(MATCH_PARENT, context.px2dp(1).toInt()).apply {
                bottomToBottom = 0
            }
            lineView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.baseLightColor
                )
            )
        }
        addView(lineView)
    }

}