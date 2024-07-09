import org.gradle.api.tasks.wrapper.Wrapper.DistributionType

plugins {
    `maven-publish`
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.dokka") version "1.5.31"
    id("org.ajoberstar.grgit") version "4.1.0"
}

group = "com.github.lion7"
version = grgit.describe()

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.glassfish.jaxb:jaxb-runtime:2.3.3")
    compileOnly("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    compileOnly("io.opentracing:opentracing-api:0.33.0")

    implementation("commons-io:commons-io:2.16.1")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

tasks {
    wrapper {
        gradleVersion = "7.2"
        distributionType = DistributionType.ALL
    }

    test {
        useJUnitPlatform()
    }

    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.get().outputDirectory)

    }
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(tasks["javadocJar"])
        }
    }
}
