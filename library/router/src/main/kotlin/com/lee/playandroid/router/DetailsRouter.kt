package com.lee.playandroid.router

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lee.playandroid.router.extensions.NavigationAnim
import com.lee.playandroid.router.extensions.navigateDeepLink
import java.net.URLEncoder

/**
 * @author jv.lee
 * @date 2021/11/5
 * @description
 */
fun NavController.navigateDetails(id: Long, title: String, url: String, isCollect: Boolean) {
    val newUrl = URLEncoder.encode(url.trim().replace("\n", "").replace("\t", ""), "UTF-8")
    val newTitle = URLEncoder.encode(title.trim().replace("\n", "").replace("\t", ""), "UTF-8")
    navigateDeepLink(
        "play://details?id=$id&title=$newTitle&url=$newUrl&isCollect=$isCollect".toUri(),
        NavigationAnim.SlideInOut
    )
}