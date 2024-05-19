plugins {
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.hilt)
}

android {
    namespace = "cn.li.core.common"
}

dependencies {
    api(libs.kotlinx.coroutines.guava)
}