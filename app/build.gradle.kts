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

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.gabrieldev.aplicacionmovcomp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.gabrieldev.aplicacionmovcomp"
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

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))

    // When using the BoM, you don't specify versions in Firebase library dependencies

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics")

    // TODO: Add the dependencies for any other Firebase products you want to use
    // See https://firebase.google.com/docs/android/setup#available-libraries
    // For example, add the dependencies for Firebase Authentication and Cloud Firestore
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
}