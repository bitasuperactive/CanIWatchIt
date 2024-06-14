plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.caniwatchitapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.caniwatchitapplication"
        minSdk = 26
        targetSdk = 34
        versionCode = 200
        versionName = "2.0-stable"
        
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

/**
 * Esta tarea transforma el BuildConfig.java del proyecto en un JSON. Se ejecuta pre-build.
 */
task("generateBuildConfigJson") {
    val outputDir =
        "${rootProject.projectDir}/app/release/"
    val outputFile = File(outputDir, "BuildConfig.json")

    doLast {
        // Crear el directorio si no existe
        outputFile.parentFile.mkdirs()

        // Obtener los valores de BuildConfig y añadir el enlace de descarga de la versión
        val buildConfigValues = mapOf(
            "applicationId" to project.android.defaultConfig.applicationId,
            "versionCode" to project.android.defaultConfig.versionCode,
            "versionName" to project.android.defaultConfig.versionName,
            "buildType" to project.android.buildTypes.getByName("release").name,
            "downloadUrl" to project.properties["appDownloadUrl"]
        )

        // Transformar Map a Json
        outputFile.writeText(groovy.json.JsonOutput.toJson(buildConfigValues))
    }
}

// Hacer que la tarea compile antes del build
tasks.named("preBuild").configure {
    dependsOn("generateBuildConfigJson")
}

dependencies {
    // [Activity]
    val activity_version = "1.9.0"
    implementation("androidx.activity:activity-ktx:$activity_version")

    // [Lifecycle]
    val lifecycle_version = "2.8.1"
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    
    // [Room]
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    
    // [Coroutines]
    val coroutines_version = "1.7.3"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    // Coroutine Lifecycle Scopes
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")

    // [Retrofit]
    val retrofit_version = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    // OkHTTP3
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")
    
    // [Navigation Components]
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    
    // [Glide]
    val glide_version = "4.11.0"
    implementation("com.github.bumptech.glide:glide:$glide_version")
    ksp("com.github.bumptech.glide:compiler:$glide_version")
    
    // [Material Design 3]
    val material3_version = "1.2.1"
    implementation("androidx.compose.material3:material3:$material3_version")
    
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}