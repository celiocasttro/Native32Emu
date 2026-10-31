plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.celiocastro.native32tv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.celiocastro.native32tv"
        minSdk = 23
        targetSdk = 28
        versionCode = 3
        versionName = "0.3.0"

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.github.BluetileEntertainment:LibretroDroid:583c580193c0bcdff686874d478d5b679f100131")
}
