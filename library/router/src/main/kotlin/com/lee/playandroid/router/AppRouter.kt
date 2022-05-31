package com.lee.playandroid.router

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import java.net.URLEncoder

/**
 * app全局路由扩展函数 (项目所有跳转都通过该扩展类来实现)
 * @author jv.lee
 * @date 2022/1/4
 */

fun NavController.navigateDeepLink(uri: Uri, anim: NavigationAnim = NavigationAnim.SlideIn) {
    navigate(NavDeepLinkRequest.Builder.fromUri(uri).build(), anim.options())
}

fun NavController.navigateDetails(
    title: String,
    url: String,
    id: Long = 0,
    isCollect: Boolean = false
) {
    val newUrl = URLEncoder.encode(url.trim().replace("\n", "").replace("\t", ""), "UTF-8")
    val newTitle = URLEncoder.encode(title.trim().replace("\n", "").replace("\t", ""), "UTF-8")
    navigateDeepLink(
        "play://details?title=$newTitle&url=$newUrl&id=$id&isCollect=$isCollect".toUri(),
        NavigationAnim.SlideInOut
    )
}

fun NavController.navigateLogin() {
    navigateDeepLink("play://login".toUri(), NavigationAnim.ZoomIn)
}

fun NavController.navigateOfficial() {
    navigateDeepLink("play://official".toUri(), NavigationAnim.ZoomIn)
}

fun NavController.navigateProject() {
    navigateDeepLink("play://project".toUri(), NavigationAnim.ZoomIn)
}

fun NavController.navigateSearch() {
    navigateDeepLink("play://search".toUri(), NavigationAnim.ZoomIn)
}

fun NavController.navigateMyShare() {
    navigateDeepLink("play://myShare".toUri(), NavigationAnim.SlideInOut)
}

fun NavController.navigateTodo() {
    navigateDeepLink("play://todo".toUri(), NavigationAnim.ZoomIn)
}