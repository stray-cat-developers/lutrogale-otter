import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jmailen.kotlinter") version "3.6.0"
    id("com.avast.gradle.docker-compose") version "0.14.9"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
    kotlin("plugin.allopen") version "1.6.10"
    kotlin("plugin.noarg") version "1.6.10"
    kotlin("kapt") version "1.6.10"
}

group = "io.mustelidae.otter.lutrogale"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    mavenCentral()
}

ext["log4j2.version"] = "2.17.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "junit", module = "junit")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-hateoas")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")

    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
    implementation("com.querydsl:querydsl-core:5.0.0")
    implementation("com.querydsl:querydsl-jpa:5.0.0")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.1.0")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.6")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.5")

    testImplementation("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java:8.0.25")

    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.hibernate:hibernate-envers")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

noArg {
    invokeInitializers = true
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()

    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                val output =
                    "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
                val startItem = "|  "
                val endItem = "  |"
                val repeatLength = startItem.length + output.length + endItem.length
                println("\n${"-".repeat(repeatLength)}\n|  $output  |\n${"-".repeat(repeatLength)}")
                println("\nElapsed: ${(result.endTime - result.startTime) / 1000} sec\n ")
            }
        }
    })
}