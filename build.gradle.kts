plugins {
    id("name.remal.sonarlint") version "3.3.11"
}
tasks.wrapper {
    gradleVersion = "8.4"
    distributionType = Wrapper.DistributionType.ALL
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
    apply(plugin = "checkstyle")
    apply(plugin = "jacoco")
    apply(plugin = "name.remal.sonarlint")
    group = "org.geekhub"

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"("org.apache.httpcomponents:httpclient:4.5.14")
        "implementation"("com.google.code.findbugs:jsr305:3.0.2")
        "implementation"(platform("org.junit:junit-bom:5.9.1"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
    }

    configure<SourceSetContainer> {
        named("main") {
            java.srcDir("src/core/java")
        }
    }

    tasks.register<JacocoCoverageVerification>("CustomJacocoTestCoverageVerification") {
        dependsOn("jacocoTestReport")
        violationRules {
            rule {
                limit {
                    minimum = BigDecimal(0.85)
                }
            }
        }
    }

    sonarLint {
        languages {
            include("java")
        }
        rules {
            disable(
                    "java:S1192", // Allow string literals to be duplicated
                    "java:S1197", // Allow constants to be defined in interfaces
                    "java:S1118", // Allow utility classes to have a private constructor
                    "java:S106", // Allow system out and err to be used
                    "java:S107", // Allow constructors with more than 7 parameters
                    "java:S3776", // Allow methods with more than 15 lines
                    "java:S1135", // Allow TODO comments
                    "java:S2094", // Allow empty classes for homeworks
                    "java:S106", // Allow system out and err to be used
            )
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
