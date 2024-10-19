/*
 * 首页BottomNavView -> Navigation扩展方法及动画修改
 * @author jv.lee
 * @date 2021/9/10
 */
package com.lee.playandroid.common.ui.extensions

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.IdRes
import androidx.core.view.forEach
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.lee.playandroid.base.extensions.endListener
import com.lee.playandroid.base.widget.nav.ExpandBottomNavigationView
import com.lee.playandroid.common.R
import com.lee.playandroid.base.R as BR
import java.lang.ref.WeakReference

inline fun ExpandBottomNavigationView.bindNavigationAction(
    container: FragmentContainerView,
    labels: Array<String>,
    crossinline itemPositionListener: (MenuItem, Int) -> Unit
) {
    val navigationInAnim =
        AnimationUtils.loadAnimation(context, BR.anim.slide_bottom_in).apply {
            endListener {
                visibility = View.VISIBLE
                clearAnimation()
            }
        }

    val navigationOutAnim =
        AnimationUtils.loadAnimation(context, BR.anim.slide_bottom_out).apply {
            endListener {
                visibility = View.GONE
                clearAnimation()
            }
        }

    post {
        val controller = container.findNavController()
        setupWithNavController2(controller, true) { menuItem, position ->
            itemPositionListener(menuItem, position)
        }

        val weakReference = WeakReference(this)
        controller.addOnDestinationChangedListener(object :
                NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = weakReference.get()
                    if (view == null) {
                        controller.removeOnDestinationChangedListener(this)
                        return
                    }
                    if (labels.contains(destination.label)) {
                        if (view.visibility == View.GONE && view.animation == null) {
                            view.startAnimation(navigationInAnim)
                        }
                    } else {
                        if (view.visibility == View.VISIBLE && view.animation == null) {
                            view.startAnimation(navigationOutAnim)
                        }
                    }
                }
            })
    }
}

fun ExpandBottomNavigationView.setupWithNavController2(
    navController: NavController,
    isNavigationAnimation: Boolean = false,
    itemPositionListener: (MenuItem, Int) -> Unit
) {
    setItemPositionListener(object : ExpandBottomNavigationView.ItemPositionListener {
        override fun onPosition(menuItem: MenuItem?, position: Int) {
            // navigation使用动画效果时不可以被点击触发
            if (animation != null && isNavigationAnimation) {
                return
            }
            menuItem?.let {
                itemPositionListener(it, position)
                onNavDestinationSelected(it, navController)
            }
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
        }
    )
}

private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
    hierarchy.any { it.id == destId }

/**
 * @see NavigationUI.onNavDestinationSelected 该方法使用动画效果切换
 * 重写该方法只使用切换功能取消动画切换逻辑
 */
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
