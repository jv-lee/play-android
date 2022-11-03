package com.lee.playandroid.base.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lee.playandroid.base.tools.SystemBarTools.compatStatusBar
import com.lee.playandroid.base.tools.SystemBarTools.statusBar

/**
 * Activity通用基类
 * @author jv.lee
 * @date 2021/6/15
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBar()
        window.compatStatusBar()
        super.onCreate(savedInstanceState)

        initSavedState(intent, savedInstanceState)

        bindView()

        bindData()
    }

    open fun initSavedState(intent: Intent, savedInstanceState: Bundle?) {}

    protected abstract fun bindView()

    protected abstract fun bindData()
}