
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("io.ktor.plugin") version "2.3.9"
}

group = "com.pacarius"
version = "0.0.1"

application {
    mainClass.set("com.pacarius.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}
ktor{
    docker{
        localImageName.set("api")
        portMappings.set(listOf(
            io.ktor.plugin.features.DockerPortMapping(9696, 9696, io.ktor.plugin.features.DockerPortMappingProtocol.TCP
                )
            )
        )
    }
}
