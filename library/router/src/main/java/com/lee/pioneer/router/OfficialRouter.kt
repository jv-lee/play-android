package com.lee.pioneer.router

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lee.pioneer.router.extensions.NavigationAnim
import com.lee.pioneer.router.extensions.navigateDeepLink

/**
 * @author jv.lee
 * @data 2021/11/5
 * @description
 */
fun NavController.navigateOfficial() {
    navigateDeepLink(
        "play-android://com.lee.playandroid/official_fragment".toUri(),
        NavigationAnim.SlideInOut
    )
}