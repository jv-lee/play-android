package com.lee.pioneer.router.extensions

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import com.lee.pioneer.router.R

/**
 * @author jv.lee
 * @data 2021/9/15
 * @description
 */

fun NavController.navigateDeepLink(uri: Uri, anim: NavigationAnim = NavigationAnim.SlideIn) {
    navigate(NavDeepLinkRequest.Builder.fromUri(uri).build(), anim.options())
}

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