import com.android.build.api.dsl.ApplicationExtension
import config.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure


/**
 * 配置 Android Application 的插件
 * */
class AndroidApplicationConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // 使用插件，如果已经应用则不会执行
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            // 配置 ApplicationConfig
            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
                @Suppress("UnstableApiUsage")
                testOptions.animationsDisabled = true

            }
        }
    }
}