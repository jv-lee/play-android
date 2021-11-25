package com.lee.playandroid.router

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lee.playandroid.router.extensions.NavigationAnim
import com.lee.playandroid.router.extensions.navigateDeepLink
import java.net.URLEncoder

/**
 * @author jv.lee
 * @data 2021/11/5
 * @description
 */
fun NavController.navigateDetails(title: String, url: String) {
    val newUrl = URLEncoder.encode(url.trim().replace("\n", "").replace("\t", ""), "UTF-8")
    val newTitle = URLEncoder.encode(title.trim().replace("\n", "").replace("\t", ""), "UTF-8")
    navigateDeepLink(
        "play://details?title=$newTitle&url=$newUrl".toUri(),
        NavigationAnim.SlideInOut
    )
}