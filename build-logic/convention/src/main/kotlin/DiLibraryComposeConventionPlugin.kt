
import com.robertomr99.atmosphere.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class DiLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("robertomr99.di.library")
            dependencies.add("implementation", libs.findLibrary("koin.compose").get())
        }
    }
}