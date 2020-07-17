import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    application
    id(Config.Plugins.KotlinJvm) version Config.kotlinVersion
    id(Config.Plugins.ShadowJar) version Config.shadowVersion
}

group = "com.christopher.pokemonService"
version = "1.0"

application {
    mainClassName = "com.christopher.pokemonService.MainKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Config.Libs.Jvm)
    implementation(Config.Libs.Ktor)
    implementation(Config.Libs.KtorClient)
    implementation(Config.Libs.KtorApache)
    implementation(Config.Libs.KtorClientGson)
    implementation(Config.Libs.KoinKtor)
    implementation(Config.Libs.KtorGson)
    implementation(Config.Libs.Logback)
    testImplementation(Config.TestLibs.Jupiter)
    testRuntimeOnly(Config.TestLibs.JupiterEngine)
    testImplementation(Config.TestLibs.Mockito)
}

tasks {
    "test" (Test::class) {
        useJUnitPlatform()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Config.javaVersion
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "Main-Class" to application.mainClassName
        ))
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}