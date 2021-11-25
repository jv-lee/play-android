package com.lee.playandroid.router

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lee.playandroid.router.extensions.NavigationAnim
import com.lee.playandroid.router.extensions.navigateDeepLink

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
fun NavController.navigateLogin() {
    navigateDeepLink("play://login".toUri(), NavigationAnim.ZoomIn)
}