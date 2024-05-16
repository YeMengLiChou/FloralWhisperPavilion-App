plugins {
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.hilt)
}

android {
    namespace = "cn.li.core.data"
}

dependencies {
    api(project(":core:common"))

    implementation(libs.kotlinx.coroutines.guava)

}