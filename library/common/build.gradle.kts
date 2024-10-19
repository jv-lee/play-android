import configures.libraryConfigure
import configures.plugins.paramsConfigure
import build.BuildModules

libraryConfigure("common", projectConfigure = {
    paramsConfigure()

    dependencies {
        commonProcessors()
        implementation(project(BuildModules.Library.base))
    }
})


