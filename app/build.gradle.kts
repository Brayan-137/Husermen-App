plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.husermenapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.husermenapp"
        minSdk = 25
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.fragment.ktx)

    // API calls
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson) // Convertidor de JSON con Gson
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    // Parse JSON
    implementation(libs.gson)

    // Show images from URL
    implementation(libs.coil)

    // Manage Youtube Videos No API
    implementation (libs.core)

    // Glide
    implementation (libs.glide)

    // MPAndroidChart
    implementation (libs.mpandroidchart)
}