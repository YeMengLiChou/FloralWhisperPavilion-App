import com.android.build.gradle.tasks.GenerateBuildConfig
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool
import java.util.Locale

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

// ksp 检查 hilt 的依赖，但是 proto 需要生成对应的类，因此需配置 ksp 在 proto 后面
afterEvaluate {
    tasks.named("kspDebugKotlin").configure {
        dependsOn(tasks.named("generateDebugProto"))
    }
    tasks.named("kspReleaseKotlin").configure {
        dependsOn(tasks.named("generateReleaseProto"))
    }
}

// 将生成的 proto 文件加入到 sourceSets 中
androidComponents.beforeVariants {
    android.sourceSets {
        val buildDir = layout.buildDirectory.get().asFile
        try {
            this[it.name].apply {
                java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
                kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
            }
        } catch (e: UnknownDomainObjectException) {
            register(it.name) {
                java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
                kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
            }
        }
    }
}




android {
    namespace = "cn.li.core.datastore"
}



dependencies {
    api(project(":core:common"))
    api(libs.protobuf.kotlin.lite)
    implementation(libs.androidx.dataStore.core)
}