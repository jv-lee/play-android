rootProject.buildFileName = "build.gradle.kts"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/jcenter/")
    }
}

include(":app")

include(":library:base")
include(":library:common")
include(":library:router")
include(":library:service")

include(":module:home")
include(":module:official")
include(":module:system")
include(":module:project")
include(":module:me")
include(":module:details")
