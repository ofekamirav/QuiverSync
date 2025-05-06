plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.quiversync.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quiversync.android"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Shared KMP module
    implementation(project(":shared"))

    // Jetpack Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.play.services.maps)
    debugImplementation(libs.compose.ui.tooling)

    // Google Maps & Maps Compose
    implementation("com.google.maps.android:maps-compose:4.2.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // RaamCosta Navigation
    implementation(libs.core)

    // KSP (for destinations or other codegen)
    ksp(libs.ksp)

    // Bottom Sheet library
    implementation(libs.bottom.sheet)
}
