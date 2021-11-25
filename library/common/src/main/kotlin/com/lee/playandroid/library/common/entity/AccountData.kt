package com.lee.playandroid.library.common.entity

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
data class AccountData(val coinInfo: CoinInfo, val userInfo: UserInfo)

data class CoinInfo(
    val coinCount: Int,
    val level: Int,
    val nickname: String,
    val rank: String,
    val userId: Long,
    val username: String
)

data class UserInfo(
    val id: Long,
    val username: String,
    val nickname: String,
    val password: String,
    val publicName: String,
    val email: String,
    val icon: String,
    val type: Int,
    val token: String,
    val admin: Boolean,
    val chapterTops: List<String>,
    val coinCount: Int,
    val collectIds: List<String>,
)