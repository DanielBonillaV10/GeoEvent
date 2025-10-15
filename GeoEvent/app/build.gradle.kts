plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "jdc.ejercicios.prueba_proyectofinal"
    compileSdk = 35

    defaultConfig {
        applicationId = "jdc.ejercicios.prueba_proyectofinal"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Dependencia de Google Maps (opcional si usas mapas)
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    // Dependencia necesaria para location services
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Dependencia de Glide para carga de imágenes
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
}
