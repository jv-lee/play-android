package com.lee.library.livedatabus

/**
 * @author jv.lee
 * @date 2019/3/30
 * @description LiveDataBus 事件监听方法注解
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectBus(
    val value: String,
    val isActive: Boolean = true,
    val isViscosity: Boolean = false
)