package com.lee.playandroid.account.constants

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
object Constants {
    // sp存储Key 登陆状态
    const val SP_KEY_IS_LOGIN = "spKey:is-login"

    // sp存储Key 账户缓存
    const val CACHE_KEY_ACCOUNT_DATA = "cacheKey:account-data"

    // sp存储key 登陆用户名（用于下次登陆自动输入）
    const val SP_KEY_SAVE_INPUT_USERNAME = "spKey:save-input-username"

    // login/register 页面回传注册key
    const val REQUEST_KEY_LOGIN = "requestKey:login"
}