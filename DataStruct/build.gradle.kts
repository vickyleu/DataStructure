import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

/*
 * Copyright lt 2022
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.cocoapods)
    id("convention.publication")
}

group = "io.github.ltttttttttttt"
//上传到mavenCentral命令: ./gradlew publishAllPublicationsToSonatypeRepository
//mavenCentral后台: https://s01.oss.sonatype.org/#stagingRepositories
version = "1.0.16"

kotlin {
    androidTarget {
        publishLibraryVariants("debug", "release")
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.jvmTarget.get()
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    js(IR) {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "DataStruct"
        browser {
            commonWebpackConfig {
                outputFileName = "DataStruct.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                        add(project.projectDir.path + "/commonMain/")
                        add(project.projectDir.path + "/wasmJsMain/")
                    }
                }
            }
        }
        binaries.executable()
    }

    cocoapods {
        summary = "DataStruct"
        homepage = "https://github.com/ltttttttttttt/DataStructure"
        ios.deploymentTarget = "14.1"
        //podfile = project.file("../ios_app/Podfile")
        framework {
            baseName = "DataStruct"
            isStatic = true
        }
        extraSpecAttributes["resources"] =
            "['src/commonMain/resources/**', 'src/desktopMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.compose.bom))
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(project.dependencies.platform(libs.kotlin.bom))
            //kotlin
            implementation(libs.kotlin.stdlib)
            //协程
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {

        }
        val desktopMain by getting {

        }
        iosMain.dependencies {

        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }

    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    namespace = "com.lt.data_structure"
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
    lint {
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
}