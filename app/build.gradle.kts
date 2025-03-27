plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    kotlin("plugin.serialization") version "2.0.0"
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
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

    // Jetpack Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // ViewModel support for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // Firebase dependencies with BoM
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google Play Services for Auth
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Hedera SDK with exclusions to avoid Protobuf conflicts
    implementation("com.hedera.hashgraph:sdk:2.29.0") {
        exclude(group = "com.google.protobuf") // Exclude protobuf-java
        exclude(group = "com.google.api.grpc", module = "proto-google-common-protos") // Exclude conflicting proto definitions
        exclude(group = "io.grpc", module = "grpc-netty-shaded")
    }

    // gRPC dependencies with exclusions
    implementation("io.grpc:grpc-kotlin-stub:1.4.1") {
        exclude(group = "com.google.protobuf")
        exclude(group = "com.google.api.grpc", module = "proto-google-common-protos")
    }
    implementation("io.grpc:grpc-protobuf:1.66.0") {
        exclude(group = "com.google.protobuf")
        exclude(group = "com.google.api.grpc", module = "proto-google-common-protos")
    }
    implementation("io.grpc:grpc-okhttp:1.66.0") {
        exclude(group = "com.google.protobuf")
        exclude(group = "com.google.api.grpc", module = "proto-google-common-protos")
    }
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Coroutines for gRPC and async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Exclude conflicting Protobuf implementations globally
configurations.configureEach {
    exclude(group = "com.google.protobuf", module = "protobuf-java")
    exclude(group = "com.google.api.grpc", module = "proto-google-common-protos")
}