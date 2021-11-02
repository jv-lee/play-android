package build

/**
 * @author jv.lee
 * @data 2021/10/1
 * @description 项目模块配置信息
 */
object BuildModules {
    const val app = ":app"

    object Library {
        const val base = ":library:base"
        const val common = ":library:common"
        const val router = ":library:router"
        const val service = ":library:service"
    }

    object Module {
        const val home = ":module:home"
        const val recommend = ":module:recommend"
        const val girl = ":module:girl"
        const val me = ":module:me"
        const val search = ":module:search"
        const val details = ":module:details"
    }

}


