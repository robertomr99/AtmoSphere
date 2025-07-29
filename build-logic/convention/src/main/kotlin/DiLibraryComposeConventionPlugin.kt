
import com.robertomr99.atmosphere.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class DiLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager){
                apply("robertomr99.di.library")
                apply("dagger.hilt.android.plugin")
            }

            dependencies{
                add("implementation", libs.findLibrary("hilt.android").get())
                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
            }

        }
    }
}