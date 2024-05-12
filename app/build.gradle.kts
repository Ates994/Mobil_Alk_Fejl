plugins {
    //alias(libs.plugins.androidApplication)
    //id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.webshopgyakorlas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.webshopgyakorlas"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)
    implementation(libs.cardview)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide)
}


