package com.lee.playandroid.router

import androidx.navigation.NavOptions
import com.lee.playandroid.router.NavigationAnim.*

/**
 * 路由页面切换动画
 * [NavigationAnim.Bottom] 底部弹起动画
 * [SlideIn] 单项侧边滑入动画
 * [SlideInOut] 侧边滑入联动侧边滑出动画
 * [ZoomIn] 缩放显示动画
 * @author jv.lee
 * @date 2022/1/4
 */
sealed class NavigationAnim {
    object Bottom : NavigationAnim()
    object SlideIn : NavigationAnim()
    object SlideInOut : NavigationAnim()
    object ZoomIn : NavigationAnim()

    fun options(): NavOptions {
        return when (this) {
            is Bottom ->
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_bottom_in)
                    .setExitAnim(R.anim.slide_default)
                    .setPopEnterAnim(R.anim.slide_default)
                    .setPopExitAnim(R.anim.slide_bottom_out)
                    .build()
            is SlideIn ->
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_right_in)
                    .setExitAnim(R.anim.slide_default)
                    .setPopEnterAnim(R.anim.slide_default)
                    .setPopExitAnim(R.anim.slide_right_out)
                    .build()
            is SlideInOut ->
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_right_in)
                    .setExitAnim(R.anim.slide_left_out)
                    .setPopEnterAnim(R.anim.slide_left_in)
                    .setPopExitAnim(R.anim.slide_right_out)
                    .build()
            is ZoomIn ->
                NavOptions.Builder()
                    .setEnterAnim(R.anim.zoom_in)
                    .setExitAnim(R.anim.zoom_exit)
                    .setPopEnterAnim(R.anim.zoom_out)
                    .setPopExitAnim(R.anim.alpha_default_hide)
                    .build()
        }
    }

}