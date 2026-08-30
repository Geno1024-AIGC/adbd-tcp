plugins {
    id("com.android.application")
}

android {
    namespace = "com.geno1024.adbtcp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.geno1024.adbtcp"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}