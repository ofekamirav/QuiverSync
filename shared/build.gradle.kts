import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("co.touchlab.skie") version "0.10.1"
    id("com.codingfeline.buildkonfig") version "0.15.1"
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
buildkonfig {
    packageName = "org.example.quiversync"

    defaultConfigs {
        buildConfigField(
          FieldSpec.Type.STRING,
            "CLOUD_NAME",
            localProperties.getProperty("cloudinary.cloudName", "")
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "UPLOAD_PRESET",
            localProperties.getProperty("cloudinary.uploadPreset", "")
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "STORM_GLASS_API_KEY",
            localProperties.getProperty("STORMGLASS_API_KEY","")

        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "GEMINI_API_KEY",
            localProperties.getProperty("GEMINI_API_KEY","")

        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "MAPS_API_KEY",
            localProperties.getProperty("MAPS_API_KEY","")
        )
    }
}


kotlin {
    sqldelight {
        databases {
            create("QuiverSyncDatabase") {
                packageName.set("org.example.quiversync")
            }
        }
    }

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
            implementation(libs.ktor.client.cio)
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
            //SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)


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
            //Cloudinary
            implementation(libs.cloudinary.android)
            //SqlDelight
            implementation(libs.sqldelight.android.driver)

        }
        iosMain.dependencies {
            //Ktor
            implementation(libs.ktor.client.darwin)
            //SqlDelight
            implementation(libs.sqldelight.native.driver)

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
