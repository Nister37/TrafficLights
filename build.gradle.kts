plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.node-gradle.node") version "7.1.0"
}

group = "be.kdg.programming5"
version = "0.0.1-SNAPSHOT"
description = "Pawel-Ryfiak-Traffic-Lights-2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // MapStruct for DTO mapping
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    runtimeOnly("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    testImplementation("org.springframework.security:spring-security-test")

    // Caching
    implementation("org.springframework.boot:spring-boot-starter-cache")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Tell the node-gradle plugin to download Node.js automatically.
// This is required in CI where the Docker image (eclipse-temurin:21-jdk-alpine)
// does not have Node.js / npm installed.
node {
    download.set(true)
    version.set("20.19.1")
}

// Run webpack before Spring copies resources into the build — ensures
// bundles in static/js/ and static/css/ are up-to-date on every build.
tasks.named<Copy>("processResources") {
    dependsOn("npm_run_build")
}
