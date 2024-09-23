plugins {
    `java-library`
    jacoco
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.assertj:assertj-core:3.20.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))
            pom {
                name.set("java-eme")
                description.set("EME (Encrypt-Mix-Encrypt) wide-block encryption for Java")
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
    repositories {
        maven {
            name = "OSSRH"
            setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = System.getenv("OSSRH_USER") ?: return@credentials
                password = System.getenv("OSSRH_PASSWORD") ?: return@credentials
            }
        }
    }
}

configure<SigningExtension> {
    val key = System.getenv("SIGNING_KEY") ?: return@configure
    val password = System.getenv("SIGNING_PASSWORD") ?: return@configure
    val publishing: PublishingExtension by project

    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
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

