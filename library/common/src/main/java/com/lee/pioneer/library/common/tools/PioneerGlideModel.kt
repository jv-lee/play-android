package com.lee.pioneer.library.common.tools

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.lee.library.net.HttpManager
import java.io.InputStream

/**
 * @author jv.lee
 * @date 2020/4/20
 * @description
 */
@GlideModule
class PioneerGlideModel : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        HttpManager.getInstance().getClient()
            ?.let {
                registry.replace(
                    GlideUrl::class.java,
                    InputStream::class.java,
                    OkHttpUrlLoader.Factory(it)
                )
            }
    }
}