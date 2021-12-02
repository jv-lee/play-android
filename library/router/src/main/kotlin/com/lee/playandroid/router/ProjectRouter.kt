package com.lee.playandroid.router

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lee.playandroid.router.extensions.NavigationAnim
import com.lee.playandroid.router.extensions.navigateDeepLink

/**
 * @author jv.lee
 * @date 2021/11/5
 * @description
 */
fun NavController.navigateProject() {
    navigateDeepLink("play://project".toUri(), NavigationAnim.SlideInOut)
}