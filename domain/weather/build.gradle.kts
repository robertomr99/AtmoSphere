plugins {
    id("robertomr99.jvm.library")
    id("robertomr99.di.library")
    alias(libs.plugins.kotlinxSerialization)
}

dependencies {
    implementation(project(":domain:region"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)
}
