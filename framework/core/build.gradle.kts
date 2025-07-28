plugins {
    id("robertomr99.android.library")
    id("robertomr99.android.room")
    id("robertomr99.jvm.retrofit")
    id("robertomr99.di.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.robertomr99.atmosphere.core"
}

dependencies {
    implementation(project(":domain:weather"))
    implementation(project(":framework:weather"))
    implementation(libs.androidx.datastore.preferences)
}