import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.5.14"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jmailen.kotlinter") version "5.5.0"
    id("com.avast.gradle.docker-compose") version "0.17.6"
    id("com.google.cloud.tools.jib") version "3.4.5"
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.spring") version "2.4.0"
    kotlin("plugin.jpa") version "2.4.0"
    kotlin("plugin.allopen") version "2.4.0"
    kotlin("plugin.noarg") version "2.4.0"
    kotlin("kapt") version "2.4.0"
}

group = "io.mustelidae.otter.lutrogale"
version = "1.1.0"

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

ext["log4j2.version"] = "2.17.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.4.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
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

    implementation("io.swagger.parser.v3:swagger-parser-v2-converter:2.1.20")
    implementation("javax.xml.bind:jaxb-api:2.3.1") // swagger-parser-v2-converter uses javax.xml.bind (removed in JDK 11+)

    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    implementation("com.querydsl:querydsl-core:5.1.0")
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:6.1.11")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.16")

    implementation("com.mysql:mysql-connector-j:9.4.0")

    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.springframework.data:spring-data-envers")
    implementation("com.google.guava:guava:33.6.0-jre")
    implementation("commons-io:commons-io:2.14.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    testImplementation("io.mockk:mockk:1.14.3")
    testImplementation("com.h2database:h2")
    implementation("com.graphql-java:graphql-java:25.0")

    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("com.github.fppt:jedis-mock:1.1.14")
}

springBoot {
    buildInfo()
}

tasks.getByName<Test>("test") {
    jvmArgs("-XX:+EnableDynamicAgentLoading") // https://github.com/mockito/mockito/issues/3037
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

tasks.register("version") {
    println(version)
}
