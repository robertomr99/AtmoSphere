plugins {
    id("robertomr99.jvm.library")
}

dependencies {
    implementation(project(":domain:region"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)
}
