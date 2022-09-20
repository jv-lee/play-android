./gradlew assembleRelease

adb shell am force-stop com.lee.playandorid
adb install -r ./app/build/outputs/apk/release/app-release.apk
adb shell am start com.lee.playandorid/.MainActivity
