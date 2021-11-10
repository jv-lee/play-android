package com.lee.playandroid.router

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lee.playandroid.router.extensions.NavigationAnim
import com.lee.playandroid.router.extensions.navigateDeepLink

/**
 * @author jv.lee
 * @data 2021/11/5
 * @description
 */
fun NavController.navigateProject() {
    navigateDeepLink(
        "play-android://com.lee.playandroid/project_fragment".toUri(),
        NavigationAnim.SlideInOut
    )
}