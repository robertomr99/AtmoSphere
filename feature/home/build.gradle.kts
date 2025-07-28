plugins {
    id("robertomr99.android.feature")
    id("robertomr99.di.library.compose")
}

android {
    namespace = "com.robertomr99.atmosphere.home"
}

dependencies {
    implementation(project(":domain:weather"))
    implementation(project(":domain:region"))
}