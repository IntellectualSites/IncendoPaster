plugins {
    java
    `java-library`
    `maven-publish`
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}

group = "com.plotsquared"
version = "1.0"

dependencies {
    compileOnlyApi("com.google.code.gson:gson:2.8.0")
    compileOnlyApi("com.google.guava:guava:21.0")
    compileOnlyApi("com.google.code.findbugs:jsr305:3.0.2")
}

repositories {
    mavenCentral()
}

val javadocDir = rootDir.resolve("docs").resolve("javadoc").resolve(project.name)
tasks {
    val assembleTargetDir = create<Copy>("assembleTargetDirectory") {
        destinationDir = rootDir.resolve("target")
        into(destinationDir)
        from(withType<Jar>())
    }
    named("build") {
        dependsOn(assembleTargetDir)
    }

    named<Delete>("clean") {
        doFirst {
            rootDir.resolve("target").deleteRecursively()
            javadocDir.deleteRecursively()
        }
    }

    compileJava {
        options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "1000"))
        options.compilerArgs.add("-Xlint:all")
        for (disabledLint in arrayOf("processing", "path", "fallthrough", "serial"))
            options.compilerArgs.add("-Xlint:$disabledLint")
        options.isDeprecation = true
        options.encoding = "UTF-8"
    }

    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.addStringOption("Xdoclint:none", "-quiet")
        opt.tags(
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:"
        )
        opt.destinationDirectory = javadocDir
    }

    jar {
        this.archiveClassifier.set("jar")
    }
}

java {
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {

                developers {
                    developer {
                        id.set("Sauilitired")
                        name.set("Alexander Söderberg")
                    }
                    developer {
                        id.set("N0tMyFaultOG")
                        name.set("NotMyFault")
                    }
                    developer {
                        id.set("SirYwell")
                        name.set("Hannes Greule")
                    }
                    developer {
                        id.set("dordsor21")
                        name.set("dordsor21")
                    }
                }

                scm {
                    url.set("https://github.com/IntellectualSites/PlotSquared")
                    connection.set("scm:https://IntellectualSites@github.com/IntellectualSites/PlotSquared.git")
                    developerConnection.set("scm:git://github.com/IntellectualSites/PlotSquared.git")
                }
            }
        }
    }

    repositories {
        mavenLocal()
        val nexusUsername: String? by project
        val nexusPassword: String? by project
        if (nexusUsername != null && nexusPassword != null) {
            maven {
                val repositoryUrl = "https://mvn.intellectualsites.com/content/repositories/releases/"
                val snapshotRepositoryUrl = "https://mvn.intellectualsites.com/content/repositories/snapshots/"
                url = uri(
                        if (version.toString().endsWith("-SNAPSHOT")) snapshotRepositoryUrl
                        else repositoryUrl
                )

                credentials {
                    username = nexusUsername
                    password = nexusPassword
                }
            }
        } else {
            logger.warn("No nexus repository is added; nexusUsername or nexusPassword is null.")
        }
    }
}
