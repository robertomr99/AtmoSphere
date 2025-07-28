plugins {
    id("robertomr99.android.feature")
    id("robertomr99.di.library.compose")
}

android {
    namespace = "com.robertomr99.atmosphere.detail"
}

dependencies {
    implementation(project(":domain:weather"))
}