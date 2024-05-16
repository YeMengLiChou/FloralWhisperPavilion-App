package config

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
/**
 * 配置 Android 中 Kotlin 参数
 * */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*,*,*,*,*,*>
) {
    commonExtension.apply {
        compileSdk = 34

        defaultConfig {
            // 最小版本
            minSdk = 26
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            isCoreLibraryDesugaringEnabled = true
        }
    }
    configureKotlin()
    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}


/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        // Up to Java 11 APIs are available through desugaring
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    configureKotlin()
}


/**
 * 配置 Kotlin 参数
 * */
private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_11.toString()

            // 协程实验性功能
//            val warningsAsErrors: String? by project
//            allWarningsAsErrors = warningsAsErrors.toBoolean()
//            freeCompilerArgs = freeCompilerArgs + listOf(
//                // Enable experimental coroutines APIs, including Flow
//                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
//            )
        }
    }
}