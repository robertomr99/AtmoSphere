plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin{
    plugins {
        register("androidApplication"){
            id = "robertomr99.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "robertomr99.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "robertomr99.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "robertomr99.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "robertomr99.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "robertomr99.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("androidRoom") {
            id = "robertomr99.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("jvmRetrofit") {
            id = "robertomr99.jvm.retrofit"
            implementationClass = "JvmRetrofitConventionPlugin"
        }
    }
}