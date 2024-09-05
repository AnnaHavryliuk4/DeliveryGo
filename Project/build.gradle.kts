plugins {
    id ("java")
    id ("io.spring.dependency-management") version "1.1.4"
    id("org.springframework.boot") version ("3.2.2")
}
val bootBuildImage by tasks.getting(org.springframework.boot.gradle.tasks.bundling.BootBuildImage::class) {
    imageName.set("coursework:latest")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation(platform("org.mockito:mockito-bom:5.7.0"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework:spring-test:5.3.16")

    implementation("org.springframework:spring-core:6.1.3")
    implementation("org.springframework:spring-context:6.1.3")
    implementation("org.springframework:spring-jdbc:6.1.3")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.flywaydb:flyway-core:10.6.0")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.6.0")

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    compileOnly("jakarta.servlet:jakarta.servlet-api:4.0.2")
}

