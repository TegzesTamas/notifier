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
}