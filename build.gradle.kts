import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    // CodeQL currently supports versions below 2.2.30
    val kotlinVersion = "2.2.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
}

group = "ru.virgil.spring"
version = "0.0.1-SNAPSHOT"

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()
}

dependencies {

    // Spring system
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.security:spring-security-messaging")
    implementation("tools.jackson.module:jackson-module-kotlin")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // DB Drivers etc
    implementation("org.postgresql:postgresql")
    implementation("com.h2database:h2")

    // Third-party dependencies
    implementation("net.datafaker:datafaker:2.5.3")
    implementation("org.awaitility:awaitility:4.3.0")
    implementation("org.awaitility:awaitility-kotlin:4.3.0")
    api("io.github.oshai:kotlin-logging-jvm:7.0.7")
    api("net.pearx.kasechange:kasechange:1.4.1")
    implementation("com.sksamuel.scrimage:scrimage-core:4.3.2")
    api("io.exoquery:pprint-kotlin:3.0.0")
    api("org.apache.tika:tika-core:3.2.0")
    api("org.apache.tika:tika-parsers:3.2.0")
    api("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
}

kotlin {
    @Suppress("SpellCheckingInspection")
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Disable bootJar task to build a plain jar, because we need to build a library, not an executable jar
tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

// Enable plain jar task, because it is disabled by default in Spring Boot projects
tasks.getByName<Jar>("jar") {
    enabled = true
}
