plugins {
    id("robertomr99.android.feature")
}

android {
    namespace = "com.robertomr99.atmosphere.detail"
}

dependencies {
    implementation(project(":domain:weather"))
}