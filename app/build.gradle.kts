plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id ("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id ("kotlin-kapt")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

android {
    namespace = "com.example.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.storyapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}
dependencies {
    // Core libraries
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.v1110)

    // UI and Layout
    implementation(libs.androidx.constraintlayout)
    implementation (libs.androidx.swiperefreshlayout)
    implementation (libs.glide)
    implementation (libs.compressor)

    // Android Support
    implementation(libs.support.annotations)
    implementation (libs.androidx.activity.ktx.v190)

    // Lifecycle and ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v280)
    implementation (libs.androidx.lifecycle.livedata.ktx.v280)

    // Room Database
    implementation (libs.androidx.room.runtime)
    implementation(libs.androidx.activity)
    ksp(libs.androidx.room.compiler)
    implementation (libs.room.paging)
    implementation (libs.room.ktx)

    // Paging
    implementation (libs.androidx.paging.runtime.ktx)

    // DataStore
    implementation (libs.androidx.datastore.preferences.v111)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Google Play Services
    implementation (libs.play.services.location)
    implementation (libs.play.services.places)
    implementation (libs.play.services.maps)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation (libs.androidx.fragment.testing)
    implementation (libs.androidx.espresso.idling.resource)
    androidTestImplementation (libs.androidx.espresso.intents)
    implementation (libs.androidx.espresso.contrib)
    androidTestImplementation (libs.androidx.core.testing)
    androidTestImplementation (libs.kotlinx.coroutines.test)
    testImplementation (libs.androidx.core.testing)
    testImplementation (libs.kotlinx.coroutines.test.v171)
    testImplementation (libs.mockito.core)
    testImplementation (libs.mockito.mockito.inline)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.strikt.core)
    androidTestImplementation (libs.mockwebserver3)
}


