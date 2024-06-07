@file:Suppress("UNCHECKED_CAST", "unused")

package com.lee.playandroid.base.tools

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType

/**
 * ViewBinding绑定工具类
 * 通过反射ViewBinding泛型获取ViewBinding实例，进行activity/fragment 基类初始化viewBinding视图
 * @see com.lee.playandroid.base.base.BaseBindingActivity
 * @see com.lee.playandroid.base.base.BaseBindingFragment
 * @see com.lee.playandroid.base.base.BaseBindingNavigationFragment
 * @see com.lee.playandroid.base.base.BaseBindingDialogFragment
 * @see com.lee.playandroid.base.base.BaseBindingAlertFragment
 * @see com.lee.playandroid.base.base.BaseBindingSheetFragment
 * @see com.lee.playandroid.base.adapter.item.ViewBindingItem
 */
object ViewBindingTools {

    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(genericOwner: Any, layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): VB =
        withGenericBindingClass<VB>(genericOwner) { clazz ->
            clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
                .invoke(null, layoutInflater, parent, attachToParent) as VB
        }.withLifecycleOwner(genericOwner)

    private fun <VB : ViewBinding> VB.withLifecycleOwner(genericOwner: Any) = apply {
        if (this is ViewDataBinding && genericOwner is ComponentActivity) {
            lifecycleOwner = genericOwner
        } else if (this is ViewDataBinding && genericOwner is Fragment) {
            lifecycleOwner = genericOwner.viewLifecycleOwner
        }
    }

    private fun <VB : ViewBinding> withGenericBindingClass(genericOwner: Any, block: (Class<VB>) -> VB): VB {
        var genericSuperclass = genericOwner.javaClass.genericSuperclass
        var superclass = genericOwner.javaClass.superclass
        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                genericSuperclass.actualTypeArguments.forEach {
                    try {
                        return block.invoke(it as Class<VB>)
                    } catch (e: NoSuchMethodException) {
                    } catch (e: ClassCastException) {
                    } catch (e: InvocationTargetException) {
                        var tagException: Throwable? = e
                        while (tagException is InvocationTargetException) {
                            tagException = e.cause
                        }
                        throw tagException ?: IllegalArgumentException("ViewBinding generic was found, but creation failed.")
                    }
                }
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }
        throw IllegalArgumentException("There is no generic of ViewBinding.")
    }
}
