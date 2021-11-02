package build

/**
 * @author jv.lee
 * @data 2021/10/1
 * @description 项目编译类型配置信息
 */

interface BuildTypes {

    companion object {
        const val DEBUG = "debug"
        const val RELEASE = "release"
    }

    val isMinifyEnabled: Boolean
    val zipAlignEnabled: Boolean
    val paramsMap: Map<String, String>
}

object BuildDebug : BuildTypes {
    override val isMinifyEnabled = false
    override val zipAlignEnabled = false
    override val paramsMap = mapOf(Pair("BASE_URI", "https://gank.io/api/v2/"))

    object SigningConfig {
        const val storeFile = "pioneer.jks"
        const val storePassword = "123456"
        const val keyAlias = "pioneer"
        const val keyPassword = "123456"
    }
}

object BuildRelease : BuildTypes {
    override val isMinifyEnabled = true
    override val zipAlignEnabled = true
    override val paramsMap = mapOf(Pair("BASE_URI", "https://gank.io/api/v2/"))

    object SigningConfig {
        const val storeFile = "pioneer.jks"
        const val storePassword = "123456"
        const val keyAlias = "pioneer"
        const val keyPassword = "123456"
    }
}
