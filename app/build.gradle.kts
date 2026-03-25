plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.charmillya.frequency"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.charmillya.frequency"
        minSdk = 24
        targetSdk = 36
        versionCode = 6
        versionName = "1.02"

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
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.compose.foundation.layout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Pour le fond material3 dans le splash
    implementation("com.google.android.material:material:1.12.0")

    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // Floutage
    implementation("dev.chrisbanes.haze:haze:1.6.4")
    implementation("dev.chrisbanes.haze:haze-materials:1.6.4")

    // Squircle
    implementation("io.github.stoyan-vuchev:squircle-shape:3.0.0")

    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")

    // Libs d'icônes
    implementation("br.com.devsrsouza.compose.icons:feather:1.1.0")
    implementation("br.com.devsrsouza.compose.icons:tabler-icons:1.1.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Pour l'encryption de tableaux dans le DS
    implementation("com.google.code.gson:gson:2.10.1")
    // Pour afficher les images des liens
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("androidx.compose.material:material-icons-extended:1.7.6")

}