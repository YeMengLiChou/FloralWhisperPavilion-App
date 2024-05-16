plugins {
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.library.compose)

}

android {
    namespace = "cn.li.core.ui"
}

dependencies {
    api(project(":core:model"))

}
