package configures

import build.*
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kapt
import appDependencies
import configures.core.freeCompilerArgs

/**
 * @author jv.lee
 * @data 2021/10/1
 * @description app模块配置依赖扩展
 */
@Suppress("MISSING_DEPENDENCY_SUPERCLASS","MISSING_DEPENDENCY_CLASS")
fun Project.appConfigure(
    projectConfigure: Project.() -> Unit = {},
    androidConfigure: BaseAppModuleExtension.() -> Unit = {}
) {
    plugins.apply(BuildPlugin.application)
    plugins.apply(BuildPlugin.kotlin)
    plugins.apply(BuildPlugin.kapt)
    plugins.apply(BuildPlugin.navigationSafeargs)

    projectConfigure()

    extensions.configure<BaseAppModuleExtension> {
        compileSdk = BuildConfig.compileSdk

        defaultConfig {
            applicationId = BuildConfig.applicationId
            minSdk = BuildConfig.minSdk
            targetSdk = BuildConfig.targetSdk
            versionName = BuildConfig.versionName
            versionCode = BuildConfig.versionCode

            multiDexEnabled = BuildConfig.multiDex

            //指定只支持中文字符
            resConfig("zh-rCN")

            testInstrumentationRunner = BuildConfig.TEST_INSTRUMENTATION_RUNNER
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs += freeCompilerArgs
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        val signingConfigs = signingConfigs.create(BuildTypes.RELEASE).apply {
            storeFile(rootProject.file(BuildRelease.SigningConfig.storeFile))
            storePassword(BuildRelease.SigningConfig.storePassword)
            keyAlias(BuildRelease.SigningConfig.keyAlias)
            keyPassword(BuildRelease.SigningConfig.keyPassword)
        }

        buildTypes {
            getByName(BuildTypes.DEBUG) {
                isMinifyEnabled = BuildDebug.isMinifyEnabled //混淆模式
                isZipAlignEnabled = BuildDebug.zipAlignEnabled
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
            }

            getByName(BuildTypes.RELEASE) {
                isMinifyEnabled = BuildRelease.isMinifyEnabled //混淆模式
                isZipAlignEnabled = BuildRelease.zipAlignEnabled
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
                signingConfig = signingConfigs
            }
        }

        buildFeatures {
            dataBinding = true
            viewBinding = true
        }

        kapt {
            generateStubs = true
        }

        androidConfigure()
    }

    dependencies {
        appDependencies()
    }

}