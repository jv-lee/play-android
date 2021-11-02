package com.lee.pioneer.router

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lee.pioneer.router.extensions.NavigationAnim
import com.lee.pioneer.router.extensions.navigateDeepLink

/**
 * @author jv.lee
 * @data 2021/9/17
 * @description
 */
fun NavController.navigateDetails(id: String, url: String) {
    navigateDeepLink(
        "pioneer-app://com.lee.pioneer/details_fragment?id=$id&url=$url".toUri(),
        NavigationAnim.SlideIn
    )
}