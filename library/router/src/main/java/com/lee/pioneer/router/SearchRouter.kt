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
fun NavController.navigateSearch() {
    navigateDeepLink(
        "pioneer-app://com.lee.pioneer/search_fragment".toUri(),
        NavigationAnim.Bottom
    )
}