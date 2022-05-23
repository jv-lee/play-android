package com.lee.library.livedatabus

import android.app.Activity
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.lee.library.livedatabus.LiveDataBus.ObserverWrapper

/**
 * @author jv.lee
 * @date 2019/3/30
 * @description 事件总线
 */
@Suppress("UNCHECKED_CAST")
class LiveDataBus private constructor() {

    companion object {
        val instance by lazy { LiveDataBus() }
    }

    /**
     * 消息通道
     */
    private val bus: HashMap<String, BusMutableLiveData<Any>> = HashMap()

    fun <T> getChannel(type: Class<T>): MutableLiveData<T> {
        if (!bus.containsKey(type.simpleName)) {
            bus[type.simpleName] = BusMutableLiveData()
        }
        return bus[type.simpleName] as MutableLiveData<T>
    }

    private class ObserverWrapper<T>(private val observer: Observer<T>) : Observer<T> {

        override fun onChanged(t: T) {
            if (isCallOnObserve()) {
                return
            }
            observer.onChanged(t)
        }

        private fun isCallOnObserve(): Boolean {
            val stackTrace = Thread.currentThread().stackTrace
            stackTrace.takeIf { it.isNotEmpty() }?.forEach { element ->
                if ("android.arch.lifecycle.LiveData" == element.className && "observeForever" == element.methodName) {
                    return true
                }
            }
            return false
        }
    }

    private class BusMutableLiveData<T> : MutableLiveData<T>() {
        private val observerMap = hashMapOf<Observer<*>, Observer<*>>()

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            try {
                hook(observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun observeForever(observer: Observer<in T>) {
            if (!observerMap.containsKey(observer)) {
                observerMap[observer] = ObserverWrapper(observer)
            }
            super.observeForever(observerMap[observer] as Observer<in T>)
        }

        override fun removeObserver(observer: Observer<in T>) {
            val realObserver: Observer<T> = if (observerMap.containsKey(observer)) {
                (observerMap.remove(observer) as Observer<T>)
            } else {
                observer as Observer<T>
            }
            super.removeObserver(realObserver)
        }

        private fun hook(@NonNull observer: Observer<in T>) {
            // get wrapper`s version
            val classLiveData = LiveData::class.java
            val fieldObservers = classLiveData.getDeclaredField("mObservers")
            fieldObservers.isAccessible = true
            val objectObservers = fieldObservers.get(this)
            val classObservers = objectObservers.javaClass
            val methodGet = classObservers.getDeclaredMethod("get", Any::class.java)
            methodGet.isAccessible = true
            val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            checkNotNull(objectWrapper) { "Wrapper can not be bull!" }
            val classObserverWrapper = objectWrapper.javaClass.superclass
            val filedLastVersion = classObserverWrapper.getDeclaredField("mLastVersion")
            filedLastVersion.isAccessible = true

            // get livedata`s version
            val fieldVersion = classLiveData.getDeclaredField("mVersion")
            fieldVersion.isAccessible = true
            val objectVersion = fieldVersion.get(this)

            // set wrapper`s version
            filedLastVersion.set(objectWrapper, objectVersion)
        }
    }

    /**
     * 订阅通知
     * @param lifecycleOwner
     */
    fun injectBus(lifecycleOwner: LifecycleOwner) {
        val aClass = lifecycleOwner.javaClass
        var viewLifecycleOwner: LifecycleOwner? = null

        if (lifecycleOwner is Activity) viewLifecycleOwner = lifecycleOwner
        if (lifecycleOwner is Fragment) viewLifecycleOwner = lifecycleOwner.viewLifecycleOwner

        // 获取当前类所有方法
        val declaredMethods = aClass.declaredMethods
        declaredMethods.forEach { method ->
            val injectBus = method.getAnnotation(InjectBus::class.java)
            injectBus?.run {
                check(method.parameterTypes.size == 1) {
                    "$aClass injectBus function params isEmpty or params count > 1."
                }
                val tagType = method.parameterTypes.first()
                val observer = Observer<Any> {
                    try {
                        method.invoke(lifecycleOwner, it)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                viewLifecycleOwner?.run {
                    val channel = instance.getChannel(tagType)
                    if (isActive) { // 是否是激活状态 激活状态last粘性消息
                        channel.observe(this, observer)
                    } else { // 非激活状态即时消息
                        channel.observeForever(observer)
                    }

                    // 解除绑定
                    lifecycle.addObserver(object : LifecycleEventObserver {
                        override fun onStateChanged(
                            source: LifecycleOwner,
                            event: Lifecycle.Event
                        ) {
                            if (event == Lifecycle.Event.ON_DESTROY) {
                                channel.removeObserver(observer)
                                viewLifecycleOwner.lifecycle.removeObserver(this)
                            }
                        }
                    })
                }
            }
        }
    }

}