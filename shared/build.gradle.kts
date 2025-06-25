import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "2.1.0"
    id("co.touchlab.skie") version "0.10.1"
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            //ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            //Koin
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.test)
            implementation(libs.koin.compose.viewmodel)
            //Date
            implementation(libs.kotlinx.datetime)
            //firebase
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.common)
            implementation(libs.firebase.storage)
            //Date
            implementation(libs.kotlinx.datetime)


        }
        androidMain.dependencies {
            implementation(libs.androidx.lifecycle.viewmodel.ktx)
            //ktor
            implementation(libs.ktor.client.okhttp)
            //koin
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            //UI
            implementation(libs.androidx.material3.window.size)
            //DataStore
            implementation(libs.androidx.datastore.preferences)

        }
        iosMain.dependencies {
            //Ktor
            implementation(libs.ktor.client.darwin)

        }
    }
}

android {
    namespace = "org.example.quiversync.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
