plugins {
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.hilt)
    alias(libs.plugins.secrets)
}

android {

    // secrets gradle plugins 生成 BuildConfig
    buildFeatures {
        buildConfig = true
    }
    namespace = "cn.li.core.network"
}

secrets {
    propertiesFileName = "secrets.defaults.properties"
    defaultPropertiesFileName = "secrets.defaults.properties"
}


dependencies {
    api(project(":core:common"))
    api(project(":core:model"))
    api(project(":core:datastore"))
    api(libs.paging.runtime)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.jackson)
    implementation(libs.jackson.kotlin.module)
    api(libs.coil.kt)
    api(libs.coil.kt.svg)
}