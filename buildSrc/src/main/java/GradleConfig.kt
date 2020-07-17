
private const val ktorVersion = "1.2.5"
private const val koinVersion = "2.0.1"
private const val jupiterVersion = "5.3.1"
private const val logbackVersion = "1.3.0-alpha5"
private const val jacksonVersion = "2.10.0"
private const val lettuceVersion = "5.2.2.RELEASE"
private const val arangoVersion = "6.6.2"
private const val velocyPackVersion = "0.1.4"
private const val apachePOIVersion = "4.1.2"

object Config {

    const val kotlinVersion = "1.3.72"
    const val javaVersion = "11"
    const val shadowVersion = "5.0.0"

    object Plugins {
        const val KotlinJvm = "org.jetbrains.kotlin.jvm"
        const val ShadowJar = "com.github.johnrengelman.shadow"
    }

    object Libs {
        const val Jvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        const val Ktor = "io.ktor:ktor-server-netty:$ktorVersion"
        const val KtorClient = "io.ktor:ktor-client-core:$ktorVersion"
        const val KtorClientGson = "io.ktor:ktor-client-gson:$ktorVersion"
        const val KtorApache = "io.ktor:ktor-client-apache:$ktorVersion"
        const val KoinKtor = "org.koin:koin-ktor:$koinVersion"
        const val KtorGson = "io.ktor:ktor-gson:$ktorVersion"
        const val Logback = "ch.qos.logback:logback-classic:$logbackVersion"
    }

    object TestLibs {
        const val Jupiter = "org.junit.jupiter:junit-jupiter-api:$jupiterVersion"
        const val JupiterEngine = "org.junit.jupiter:junit-jupiter-engine:$jupiterVersion"
        const val Mockito = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    }
}