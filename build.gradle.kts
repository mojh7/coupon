import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.8"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
    id ("org.sonarqube") version "3.2.0"
    kotlin("jvm") version "1.5.32"
    kotlin("plugin.spring") version "1.5.32"
    kotlin("plugin.jpa") version "1.5.32"
    jacoco
}

group = "com.mojh"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

val kotestVersion = "5.2.1"

dependencies {
    // web
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // jjwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    // kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // mysql
    runtimeOnly("mysql:mysql-connector-java")

    // redis
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")

    // embedded-redis
    implementation ("it.ozimov:embedded-redis:0.7.2")

    // h2
    implementation("com.h2database:h2")

    // log4j2
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    // test
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.mockito")
    }
    testImplementation("com.ninja-squad:springmockk:3.0.1")

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property:$kotestVersion") // for kotest property test
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0") // spring extensions
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations {
    all {
        /**
         * spring-boot-starter-web에 logging 모듈이 추가되어있음
         * spring에서 지원하는 기본 로깅 모듈은 logBack 이기 때문에
         * log4j2 사용을 위해 기본 로깅 제외
         */
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

extra["snippetsDir"] = file("build/generated-snippets")

extra["kotlin-coroutines.version"] = "1.6.0"

tasks.test {
    project.property("snippetsDir")?.let { outputs.dir(it) }

    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$buildDir/jacoco/jacoco.exec"))
    }
}

tasks.asciidoctor {
    project.property("snippetsDir")?.let { inputs.dir(it) }
    dependsOn(tasks.test)
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = false

        html.destination = file("$buildDir/jacoco/jacocoHtml")
        xml.destination = file("$buildDir/jacoco/jacocoTest.xml")
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = 0.00.toBigDecimal()
            }

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = 0.00.toBigDecimal()
            }
        }
    }
}

sonarqube {
    properties {
        property ("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/jacoco/jacocoTest.xml")
    }
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}
allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}