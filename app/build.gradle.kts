/*
Generally,
a build script (build.gradle(.kts)) details
build configuration, tasks, and plugins.
*/
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
//    libreria Kotlin Symbol Processing
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.gabrieldev.alfabetizaciondigitalarearural"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.gabrieldev.alfabetizaciondigitalarearural"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    val roomVersion = "2.8.4"
    //libreria de navegacion - navigation
    implementation(libs.androidx.navigation.compose)
    //libreria para la bd - room
    implementation("androidx.room:room-runtime:${roomVersion}")
    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$roomVersion")
    // Coroutine Image Loader, imagenes cargadas en segundo plano
    implementation(libs.coil.compose)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:${roomVersion}")
    // Iconos extendidos (para el Rayo)
    implementation("androidx.compose.material:material-icons-extended")

    // dependencias Nearby Connections
    implementation("com.google.android.gms:play-services-nearby:19.3.0")

    // GSON
    implementation("com.google.code.gson:gson:2.10.1")

    //Libreria WorkManager
    implementation("androidx.work:work-runtime:2.11.1")
}