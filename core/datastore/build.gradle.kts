plugins {
    alias(libs.plugins.li.android.library)
    alias(libs.plugins.li.android.hilt)
    alias(libs.plugins.protobuf)
}

// 配置 protobuf，生成对应的类文件
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

android {
    namespace = "cn.li.core.datastore"
}



dependencies {
    api(libs.protobuf.kotlin.lite)
    implementation(libs.androidx.dataStore.core)
    implementation(project(":core:common"))
}