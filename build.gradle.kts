plugins {
    kotlin("js") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
}

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "carcare.js"
            }
            runTask {
                devServer = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer(
                    port = 5000,
                    open = false,
                    static = mutableListOf("$rootDir/src/main/resources"),
                )
            }
        }
    }
}

dependencies {
    // kotlin-wrappers version is date-stamped and moves fast - if this exact version is gone
    // from Maven Central by the time you build, bump it to whatever's latest at
    // https://github.com/JetBrains/kotlin-wrappers/releases (just the wrappers BOM version).
    implementation(platform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.797"))
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}
