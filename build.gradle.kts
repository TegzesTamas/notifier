plugins {
    application
    kotlin("jvm").version("1.2.61")
}

repositories {
    jcenter()
}

application{
    mainClassName = "hu.tegzes.tamas.notifier.MainKt"
}

dependencies {
    compile(kotlin("stdlib"))
    implementation("com.beust:klaxon:3.0.1")
    implementation("com.github.magneticflux:kotlin-simplexml-rss:1.1.2")
}
