./gradlew assembleRelease

adb shell am force-stop com.lee.playandroid
adb install -r ./app/build/outputs/apk/release/app-release.apk
adb shell am start com.lee.playandroid/.MainActivity
