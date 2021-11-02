import configures.libraryConfigure
import configures.plugins.paramsConfigure
import build.BuildModules

libraryConfigure(projectConfigure = {
    paramsConfigure()

    dependencies {
        commonProcessors()
        implementation(project(BuildModules.Library.base))
    }
})


