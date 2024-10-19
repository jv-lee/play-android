package configures

import build.BuildConfig
import build.BuildPlugin
import com.android.build.gradle.LibraryExtension
import configures.core.freeCompilerArgs
import kapt
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * 基础库配置依赖扩展
 * @author jv.lee
 * @date 2021/10/1
 */
fun Project.libraryConfigure(
    name: String,
    projectConfigure: Project.() -> Unit = {},
    androidConfigure: LibraryExtension.() -> Unit = {}
) {
    plugins.apply(BuildPlugin.library)
    plugins.apply(BuildPlugin.kotlin)
    plugins.apply(BuildPlugin.kapt)
    plugins.apply(BuildPlugin.parcelize)

    projectConfigure()

    extensions.configure<LibraryExtension> {
        namespace = "${BuildConfig.applicationId}.$name"
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

        androidConfigure()
    }

}