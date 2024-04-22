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
//需要判断是否是jitpack的构建，如果是jitpack的构建，需要将build目录设置到项目根目录下
if (System.getenv("JITPACK") == null) {
    rootProject.layout.buildDirectory.set(file("./build"))
}

plugins{
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.jetbrainsCompose).apply(false)
    alias(libs.plugins.cocoapods).apply(false)
}



val javaVersion = JavaVersion.toVersion(libs.versions.jvmTarget.get())
check(JavaVersion.current().isCompatibleWith(javaVersion)) {
    "This project needs to be run with Java ${javaVersion.getMajorVersion()} or higher (found: ${JavaVersion.current()})."
}

allprojects {
    tasks.register("testClasses")
}
subprojects {
    if (System.getenv("JITPACK") == null) {
        this.layout.buildDirectory.set(file("${rootProject.layout.buildDirectory.get().asFile.absolutePath}/${project.name}"))
    }
    configurations.all {
        exclude(group = "org.jetbrains.compose.material", module = "material")
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.jetbrains.kotlin") {
                    useVersion(libs.versions.kotlin.get())
                }else if (requested.group.startsWith("org.jetbrains.compose.")
                    && !requested.group.endsWith(".compiler")) {
                    useVersion(libs.versions.compose.plugin.get())
                }else if (requested.group == "org.jetbrains" && requested.name == "annotations") {
                    useVersion(libs.versions.annotations.get())
                }
            }
        }
    }
}