plugins {
    alias(libs.plugins.li.android.feature)
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.library.compose)
}

android {
    namespace = "cn.li.feature.mine"
}

dependencies {
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.contraintLayout.compose)
}