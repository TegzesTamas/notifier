plugins {
    application
    kotlin("jvm").version("1.3.11")
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/kotlin/exposed")
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
    compile(group = "org.xerial", name = "sqlite-jdbc", version = "3.20.1")
    compile(group = "org.jetbrains.exposed", name = "exposed", version = "0.11.2")
    compile(group = "org.slf4j", name = "slf4j-log4j12", version = "1.7.25")
}
