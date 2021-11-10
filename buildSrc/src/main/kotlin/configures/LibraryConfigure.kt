package configures

import build.BuildConfig
import build.BuildPlugin
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kapt
import configures.core.freeCompilerArgs

/**
 * @author jv.lee
 * @data 2021/10/1
 * @description 基础库配置依赖扩展
 */
@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
fun Project.libraryConfigure(
    projectConfigure: Project.() -> Unit = {},
    androidConfigure: LibraryExtension.() -> Unit = {}
) {
    plugins.apply(BuildPlugin.library)
    plugins.apply(BuildPlugin.kotlin)
    plugins.apply(BuildPlugin.kapt)
    plugins.apply(BuildPlugin.parcelize)

    projectConfigure()

    extensions.configure<LibraryExtension> {
        compileSdk = BuildConfig.compileSdk

        defaultConfig {
            minSdk = BuildConfig.minSdk
            targetSdk = BuildConfig.targetSdk
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

        sourceSets {
            getByName("main") {
                assets {
                    srcDir("src/main/assets")
                }
            }
        }

        kapt {
            generateStubs = true
        }

        androidConfigure()
    }

}