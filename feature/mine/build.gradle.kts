plugins {
    alias(libs.plugins.li.android.feature)
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.library.compose)
}

android {
    namespace = "cn.li.feature.mine"
}

dependencies {
    implementation(project(":core:data"))

}