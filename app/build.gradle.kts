import java.util.Properties

plugins {
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    id("robertomr99.android.application")
    id("robertomr99.android.application.compose")
    id("robertomr99.di.library.compose")
}

android {
    namespace = "com.robertomr99.atmosphere"

    defaultConfig {
        applicationId = "com.robertomr99.atmosphere"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").readText().byteInputStream())

        val owApiKey = properties.getProperty("OW_API_KEY", "")
        buildConfigField("String", "OW_API_KEY", "\"$owApiKey\"")
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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain:weather"))
    implementation(project(":domain:region"))
    implementation(project(":framework:core"))
    implementation(project(":framework:weather"))
    implementation(project(":framework:region"))
    implementation(project(":feature:home"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:common"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}