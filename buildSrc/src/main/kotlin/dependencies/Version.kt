package dependencies

object Version {

    //core
    const val ktxCore = "1.7.0"
    const val coroutines = "1.5.2"
    const val multidex = "2.0.1"
    const val appcompat = "1.4.0"
    const val material = "1.4.0"
    const val recyclerview = "1.2.1"
    const val constraintLayout = "2.1.1"
    const val viewpager2 = "1.0.0"
    const val swipeRefreshLayout = "1.1.0"
    const val slidingPaneLayout = "1.1.0" // 1.1.0正常使用 ， 1.2.0无法正常显示
    const val activity = "1.4.0"
    const val fragment = "1.4.0"
    const val navigation = "2.4.0"
    const val lifecycle = "2.4.0"
    const val room = "2.4.0"
    const val glide = "4.11.0"
    const val retrofit = "2.6.0"
    const val gson = "2.8.8"
    const val protobuf = "3.10.0"
    const val autoService = "1.0"

    //Test
    const val junit = "4.13.2"
    const val junitAndroid = "1.1.2"
    const val espressoAndroid = "3.3.0"
    const val leakcanary = "2.9.1"
    const val blockCanary =
        "1.5.0" // compileSdk = 32 即android12 无法编译通过 (manifest <activity> has intent-filter exported = true )
}
