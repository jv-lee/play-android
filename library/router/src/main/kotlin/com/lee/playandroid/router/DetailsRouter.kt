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
fun NavController.navigateDetails(title: String, url: String) {
    val newUrl = url.trim().replace("\n", "").replace("\t", "")
    val newTitle = title.trim().replace("\n", "").replace("\t", "")
    navigateDeepLink(
        "play-android://com.lee.play-android/details_fragment?title=$newTitle&url=$newUrl".toUri(),
        NavigationAnim.SlideInOut
    )
}