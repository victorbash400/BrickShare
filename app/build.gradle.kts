plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.brickshare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.brickshare"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" // Matches Kotlin 2.0.20, commonly used with Android Studio
    }
}

dependencies {
    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.androidx.material.icons.extended)

    // Jetpack Compose dependencies from the manual
    implementation(platform(libs.androidx.compose.bom)) // Keeps Compose libraries in sync
    implementation(libs.androidx.ui)                     // Core Compose UI
    implementation(libs.androidx.ui.graphics)           // Graphics utilities
    implementation(libs.androidx.ui.tooling.preview)    // Preview support
    implementation(libs.androidx.material3)             // Material 3 components
    implementation(libs.material3) // Latest Material 3 as of March 2025
    implementation(libs.androidx.navigation.compose) // Navigation Compose from the manual

    // ViewModel support for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose) // For ViewModel integration


    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)       // For preview tooling
    debugImplementation(libs.androidx.ui.test.manifest) // Test manifest support
}