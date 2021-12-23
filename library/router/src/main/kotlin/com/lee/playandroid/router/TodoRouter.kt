package com.lee.playandroid.router

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lee.playandroid.router.extensions.NavigationAnim
import com.lee.playandroid.router.extensions.navigateDeepLink

/**
 * @author jv.lee
 * @date 2021/12/23
 * @description
 */
fun NavController.navigateTodo() {
    navigateDeepLink("play://todo".toUri(), NavigationAnim.SlideInOut)
}