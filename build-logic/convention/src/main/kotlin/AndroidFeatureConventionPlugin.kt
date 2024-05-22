import com.android.build.gradle.LibraryExtension
import config.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("li.android.library")
                apply("li.android.hilt")
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    testOptions.animationsDisabled = true
                }
            }
            dependencies {
                add("api", project(":core:ui"))
                add("api", project(":core:model"))
                add("api", project(":core:data"))
                add("api", project(":core:common"))

                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
            }
        }
    }
}