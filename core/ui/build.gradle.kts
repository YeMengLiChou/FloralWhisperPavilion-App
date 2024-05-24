plugins {
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.library.compose)

}

android {
    namespace = "cn.li.core.ui"
}

dependencies {
    api(project(":core:model"))
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)
    api(libs.coil.kt.compose)
    api(libs.androidx.contraintLayout.compose)
    api(libs.androidx.activity.compose)
}
