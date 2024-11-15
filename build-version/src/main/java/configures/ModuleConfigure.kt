package configures

import build.BuildConfig
import build.BuildModules
import build.BuildPlugin
import com.android.build.gradle.LibraryExtension
import freeCompilerArgs
import implementation
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * 组件模块配置依赖扩展
 * @author jv.lee
 * @date 2021/10/1
 */
fun Project.moduleConfigure(name: String, projectConfigure: Project.() -> Unit = {}) {
    plugins.apply(BuildPlugin.LIBRARY)
    plugins.apply(BuildPlugin.KOTLIN_ANDROID)
    plugins.apply(BuildPlugin.KOTLIN_KAPT)

    extensions.configure<LibraryExtension> {
        namespace = "${BuildConfig.APPLICATION_ID}.$name"
        compileSdk = BuildConfig.COMPILE_SDK

        defaultConfig {
            minSdk = BuildConfig.MIN_SDK
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs += freeCompilerArgs
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        buildFeatures {
            dataBinding = true
            viewBinding = true
        }

    }

    dependencies {
        // 添加基础服务依赖
        implementation(project(BuildModules.Library.SERVICE))
    }

    projectConfigure()
}