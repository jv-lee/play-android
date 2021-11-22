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
fun NavController.navigateDetails(title:String,url:String) {
    navigateDeepLink(
        "play-android://com.lee.play-android/details_fragment?title=$title&url=$url".toUri(),
        NavigationAnim.SlideInOut
    )
}