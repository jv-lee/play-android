package com.lee.playandroid.library.common.tools

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import com.google.android.material.navigation.NavigationBarView
import com.lee.library.widget.nav.BottomNavView
import java.lang.ref.WeakReference

/**
 * @author jv.lee
 * @data 2021/9/10
 * @description 取消navigation 切换动画效果
 */
fun BottomNavView.setupWithNavController2(
    navController: NavController,
    itemPositionListener: (MenuItem, Int) -> Unit
) {
    setItemPositionListener(object : BottomNavView.ItemPositionListener {
        override fun onPosition(menuItem: MenuItem, position: Int) {
            itemPositionListener(menuItem, position)
            onNavDestinationSelected(
                menuItem,
                navController
            )
        }
    })
    val weakReference = WeakReference(this)
    navController.addOnDestinationChangedListener(
        object : NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                val view = weakReference.get()
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this)
                    return
                }
                view.menu.forEach { item ->
                    if (destination.matchDestination(item.itemId)) {
                        item.isChecked = true
                    }
                }
            }
        })

}


private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
    hierarchy.any { it.id == destId }

private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
    val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
    if (item.order and Menu.CATEGORY_SECONDARY == 0) {
        builder.setPopUpTo(
            navController.graph.findStartDestination().id,
            inclusive = false,
            saveState = true
        )
    }
    val options = builder.build()
    return try {
        // TODO provide proper API instead of using Exceptions as Control-Flow.
        navController.navigate(item.itemId, null, options)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

