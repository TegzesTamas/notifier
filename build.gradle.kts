plugins {
    application
    kotlin("jvm").version("1.2.61")
}

repositories {
    jcenter()
}

application {
    mainClassName = "hu.tegzes.tamas.notifier.MainKt"
}

dependencies {
    compile(kotlin("stdlib"))
    implementation("com.beust:klaxon:3.0.1")
    implementation("com.github.magneticflux:kotlin-simplexml-rss:1.1.2")
    compile("com.google.api-client:google-api-client:1.23.0")
    compile("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    compile("com.google.apis:google-api-services-gmail:v1-rev83-1.23.0")
    compile(group = "com.sun.mail", name = "javax.mail", version = "1.6.2")
}
