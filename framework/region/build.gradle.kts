plugins {
    id("robertomr99.android.library")
    id("robertomr99.di.library")
}
android {
    namespace = "com.robertomr99.atmosphere.region"
}

dependencies {
    implementation(project(":domain:region"))
    implementation(libs.play.services.location)
}