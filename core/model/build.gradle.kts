plugins {
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.library.compose)
}

android {
    namespace = "li.cn.core.model"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
}