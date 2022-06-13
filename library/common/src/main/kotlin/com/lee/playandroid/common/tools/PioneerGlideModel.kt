package com.lee.playandroid.common.tools

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.lee.playandroid.base.net.HttpManager
import java.io.InputStream

/**
 * @author jv.lee
 * @date 2020/4/20
 */
@GlideModule
class PioneerGlideModel : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        HttpManager.instance.getClient().let {
            registry.replace(
                GlideUrl::class.java,
                InputStream::class.java,
                OkHttpUrlLoader.Factory(it)
            )
        }
    }
}