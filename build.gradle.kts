import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
}

group = "ru.virgil.spring"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

@Suppress("SpellCheckingInspection")
dependencies {

    // Spring system
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.security:spring-security-test")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("stdlib-jdk8"))

    // DB Drivers etc
    implementation("org.postgresql:postgresql")
    implementation("com.h2database:h2")
    implementation("com.hazelcast:hazelcast")

    // Third-party dependencies
    api("net.datafaker:datafaker:1.9.0")
    api("com.google.truth:truth:1.1.3")
    api("org.apache.tika:tika-core:2.7.0")
    api("org.apache.tika:tika-parsers:2.7.0")
    api("com.google.firebase:firebase-admin:9.1.1")
    api("io.kotest:kotest-assertions-core:5.6.1")
    testApi("io.kotest:kotest-assertions-core:5.6.1")
}

@Suppress("SpellCheckingInspection")
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
