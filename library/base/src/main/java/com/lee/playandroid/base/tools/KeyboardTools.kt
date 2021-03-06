package com.lee.playandroid.base.tools

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.extensions.setMargin
import com.lee.playandroid.base.extensions.statusBarHeight

/**
 * 避免输入法面板遮挡
 * 在manifest.xml中activity中设置
 * android:windowSoftInputMode="adjustPan"
 * @author jv.lee
 * @date 2020/9/4
 */
object KeyboardTools {

    var imm: InputMethodManager =
        app.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    /**
     * 动态显示软键盘
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    fun Context.showSoftInput() {
        val activity = (this as? Activity) ?: return
        val view = activity.window.decorView
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        imm.showSoftInput(view, 0)
    }

    /**
     * 动态隐藏软键盘
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    fun Context.hideSoftInput() {
        var view = (this as? Activity)?.currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 判断软键盘是否弹出
     */
    fun Context.keyboardIsShow(): Boolean {
        val windowToken = (this as? Activity)?.window?.decorView?.windowToken ?: return false
        return imm.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * 切换键盘显示与否状态
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    fun Context.toggleSoftInput() {
        if (keyboardIsShow()) {
            hideSoftInput()
        } else {
            showSoftInput()
        }
    }

    /**
     * 动态显示软键盘
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    fun View.showSoftInput() {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        imm.showSoftInput(this, 0)
    }

    /**
     * 沉浸式状态栏 设置adjustResize 后 解决软键盘无法正常顶起解决方式
     */
    fun ViewGroup.adjustResizeStatusBar(
        window: Window,
        marginValue: Int = context.statusBarHeight
    ) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        fitsSystemWindows = true
        setMargin(top = -marginValue)
    }

    /**
     * 点击任意view隐藏输入法
     */
    @SuppressLint("ClickableViewAccessibility")
    fun View.parentTouchHideSoftInput() {
        setOnTouchListener { _, _ ->
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            if (context.keyboardIsShow()) {
                context.hideSoftInput()
            }
            false
        }
    }

    /**
     * 监听键盘弹起更该viewPaddingBottom值
     */
    fun View.keyboardPaddingBottom(lifecycleOwner: LifecycleOwner? = findViewTreeLifecycleOwner()) {
        val keyboardMinHeight = 100
        var initDiff = 0
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            getWindowVisibleDisplayFrame(rect)

            val height: Int = context.resources.displayMetrics.heightPixels
            // 获取键盘抬高的高度
            val diff: Int = height - rect.height()
            if (diff > keyboardMinHeight) {
                setPadding(0, 0, 0, diff - initDiff)
            } else {
                initDiff = diff
                setPadding(0, 0, 0, 0)
            }
        }
        lifecycleOwner?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        viewTreeObserver.addOnGlobalLayoutListener(listener)
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        viewTreeObserver.removeOnGlobalLayoutListener(listener)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        source.lifecycle.removeObserver(this)
                    }
                    else -> {
                    }
                }
            }
        })
    }

    /**
     * 监听键盘弹起
     */
    inline fun View.keyboardObserver(
        lifecycleOwner: LifecycleOwner? = findViewTreeLifecycleOwner(),
        crossinline open: () -> Unit = {},
        crossinline close: () -> Unit = {},
    ) {
        var isOpen = false
        val keyboardHeight = 200
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            getWindowVisibleDisplayFrame(rect)

            val height: Int = context.resources.displayMetrics.heightPixels
            // 获取键盘抬高的高度
            val diff: Int = height - rect.height()
            if (diff > keyboardHeight && !isOpen) {
                isOpen = true
                open()
            } else if (diff < keyboardHeight && isOpen) {
                isOpen = false
                close()
            }
        }
        lifecycleOwner?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        viewTreeObserver.addOnGlobalLayoutListener(listener)
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        viewTreeObserver.removeOnGlobalLayoutListener(listener)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        source.lifecycle.removeObserver(this)
                    }
                    else -> {
                    }
                }
            }
        })
    }

}