package com.lee.playandroid.me

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val list = arrayListOf<String>("a", "b", "c")
        val string = list.reduce { acc, item -> acc.plus(item) }
        println(string)
    }
}