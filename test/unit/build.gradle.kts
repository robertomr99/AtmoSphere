plugins {
    id("robertomr99.jvm.library")
}

dependencies {
    implementation(project(":domain:weather"))
    implementation(project(":domain:region"))
    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.datastore.preferences.core)
}