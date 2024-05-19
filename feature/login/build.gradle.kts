plugins {
    alias(libs.plugins.li.android.feature)
    alias(libs.plugins.li.android.library.compose)
}

android {
    namespace = "cn.li.feature.login"
}

dependencies {
    api(project(":core:data"))
    api(project(":core:ui"))

}