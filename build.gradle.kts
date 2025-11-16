plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6"
    id("com.diffplug.spotless") version "8.0.0"
    id("org.openapi.generator") version "7.16.0"
    id("com.google.cloud.tools.jib") version "3.5.0"
    id("org.jooq.jooq-codegen-gradle") version "3.20.8"
    id("com.github.ben-manes.versions") version "0.53.0"
}


group = "er.codes"
version = "0.0.1-SNAPSHOT"
description = "SecretSalty-minimal"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

val jooqVer = "3.20.9"
val springdocVer = "2.8.12"
val kotlinLoggingVer = "7.0.13"
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springdocVer}")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVer")

    implementation("com.okta.spring:okta-spring-boot-starter:3.0.7")

    // jooq
    implementation("org.jooq:jooq:$jooqVer")
    implementation("org.jooq:jooq-kotlin:$jooqVer")
    implementation("org.jooq:jooq-kotlin-coroutines:$jooqVer")
    implementation("org.jooq:jooq-postgres-extensions:$jooqVer")
    jooqCodegen("org.jooq:jooq-postgres-extensions:$jooqVer")
    jooqCodegen("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/jooq/**") // jooq generated code
        ktlint()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
    typescript {
        target("saltycasino-front/src/*.ts")
        targetExclude("**/generated/**", "node_modules/**") // openapi generated code
        prettier()
    }
    css {
        target("**/*.css")
        prettier()
    }
    yaml {
        target("**/*.yml", "**/*.yaml")
        prettier()
    }
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost:5432/secretsalty_db"
            user = "secretsalty"
            password = "totally-secure-password"
        }
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
                // ignore stuff from flyway and citext extension
                excludes = """
                    flyway_schema_history |
                    regex.* |
                    regexp.* |
                    citext.* |
                    textic.*"""
            }
            target {
                packageName = "er.codes.secretsaltyminimal.jooq"
                directory = "$projectDir/src/main/kotlin/"
            }
            generate {
                daos = true
                springDao = true
            }
        }
    }
}

openApiGenerate {
    generatorName.set("typescript-fetch")
    inputSpec.set("$rootDir/src/main/resources/openapi-doc.json") // path to your OpenAPI spec
    outputDir.set("$rootDir/frontend/src/generated") // output directory
}

jib {
    from {
        image = "eclipse-temurin:21-jre-jammy"
        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "localhost:5000/secretsalty-minimal-backend"
    }
    container {
        mainClass = "er.codes.secretsalty.SecretSaltyApplicationKt"
        ports = listOf("8080")
    }
    setAllowInsecureRegistries(true)
}
