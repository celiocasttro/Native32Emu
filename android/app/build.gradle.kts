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
        versionCode = 1
        versionName = "0.1.0"
    }
}
