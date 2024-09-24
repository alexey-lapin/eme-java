plugins {
    id("java-library")
    id("jacoco")
    id("maven-publish")
    id("signing")
    alias(libs.plugins.nexus)
    alias(libs.plugins.release)
}

group = "com.github.alexey-lapin.eme-cipher"
version = scmVersion.version
description = "EME (Encrypt-Mix-Encrypt) wide-block encryption for Java"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    testImplementation(libs.assertj)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))
            pom {
                name.set(project.name)
                description.set(provider { project.description })
                url.set("https://github.com/alexey-lapin/eme-java")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("alexey-lapin")
                        name.set("Alexey Lapin")
                        email.set("alexey-lapin@protonmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:alexey-lapin/eme-java.git")
                    developerConnection.set("scm:git:git@github.com:alexey-lapin/eme-java.git")
                    url.set("https://github.com/alexey-lapin/eme-java")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("OSSRH_USER") ?: return@sonatype)
            password.set(System.getenv("OSSRH_PASSWORD") ?: return@sonatype)
        }
    }
}

signing {
    val key = System.getenv("SIGNING_KEY") ?: return@signing
    val password = System.getenv("SIGNING_PASSWORD") ?: return@signing
    val publishing: PublishingExtension by project

    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
}
