plugins {
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.hilt)
}

android {
    namespace = "cn.li.core.data"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:datastore"))
    api(project(":core:database"))
    api(project(":core:network"))
    api(project(":core:model"))

    implementation(libs.kotlinx.coroutines.guava)

}