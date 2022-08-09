import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("io.github.amanshuraikwar.config")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":common"))

                implementation(project(":localdatasource"))
                implementation(project(":repository"))
                implementation(Libs.Coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":test-util"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(project(":sqldelightdb"))
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting
        val iosTest by getting
    }
}