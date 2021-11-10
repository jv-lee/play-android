package com.lee.playandroid.library.common.tools

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lee.library.tools.DarkModeTools
import com.lee.playandroid.library.common.R

/**
 * @author jv.lee
 * @date 2020/3/30
 * @description
 */
class GlideTools {

    private lateinit var optionsCommand: RequestOptions

    companion object {
        @Volatile
        private var instance: GlideTools? = null

        @JvmStatic
        fun get() = instance ?: synchronized(this) {
            instance ?: GlideTools().also { instance = it }
        }
    }

    init {
        initOptions()
    }

    private fun initOptions() {
        //初始化普通加载
        RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.IMMEDIATE)
            .dontTransform()
            .dontAnimate()
            .also { optionsCommand = it }
    }

    @SuppressLint("CheckResult")
    fun loadImage(
        path: String?,
        imageView: ImageView,
        @DrawableRes placeholderResId: Int =
        //glide内部没有进行图片模式判断 所以自行根据深色模式设置占位图
            if (DarkModeTools.get().isDarkTheme())
                R.mipmap.ic_picture_placeholder_night
            else
                R.mipmap.ic_picture_placeholder
    ) {
        val request = Glide.with(imageView.context)
            .load(http2https(path))
            .apply(optionsCommand.placeholder(placeholderResId))

//        //动画效果
//        request.transition(DrawableTransitionOptions.withCrossFade())

        request.into(imageView)
    }

    private fun http2https(path: Any?): Any? {
        path?.let {
            if (path is String && path.startsWith("http://")) {
                return path.replace("http://", "https://")
            }
        }
        return path
    }

}