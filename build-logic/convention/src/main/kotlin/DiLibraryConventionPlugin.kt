
import com.robertomr99.atmosphere.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class DiLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            dependencies {
                add("implementation", libs.findLibrary("hilt.core").get())
                add("ksp", libs.findLibrary("hilt.compiler").get())
            }
        }
    }
}