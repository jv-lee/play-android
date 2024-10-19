package com.lee.playandroid.router

import androidx.navigation.NavOptions
import com.lee.playandroid.router.NavigationAnim.*
import com.lee.playandroid.base.R as BR

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
    data object Bottom : NavigationAnim()
    data object SlideIn : NavigationAnim()
    data object SlideInOut : NavigationAnim()
    data object ZoomIn : NavigationAnim()

    fun options(): NavOptions {
        return when (this) {
            is Bottom ->
                NavOptions.Builder()
                    .setEnterAnim(BR.anim.slide_bottom_in)
                    .setExitAnim(BR.anim.slide_default)
                    .setPopEnterAnim(BR.anim.slide_default)
                    .setPopExitAnim(BR.anim.slide_bottom_out)
                    .build()
            is SlideIn ->
                NavOptions.Builder()
                    .setEnterAnim(BR.anim.slide_right_in)
                    .setExitAnim(BR.anim.slide_default)
                    .setPopEnterAnim(BR.anim.slide_default)
                    .setPopExitAnim(BR.anim.slide_right_out)
                    .build()
            is SlideInOut ->
                NavOptions.Builder()
                    .setEnterAnim(BR.anim.slide_right_in)
                    .setExitAnim(BR.anim.slide_left_out)
                    .setPopEnterAnim(BR.anim.slide_left_in)
                    .setPopExitAnim(BR.anim.slide_right_out)
                    .build()
            is ZoomIn ->
                NavOptions.Builder()
                    .setEnterAnim(BR.anim.zoom_in)
                    .setExitAnim(BR.anim.zoom_exit)
                    .setPopEnterAnim(BR.anim.zoom_out)
                    .setPopExitAnim(BR.anim.alpha_default_hide)
                    .build()
        }
    }
}