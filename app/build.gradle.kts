import com.android.build.api.variant.VariantOutputConfiguration

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

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.outputs
            .filter { it.outputType == VariantOutputConfiguration.OutputType.SINGLE }
            .forEach { output ->
                output.outputFileName.set("adbd-tcp-v1.0-${variant.buildType}.apk")
            }
    }
}