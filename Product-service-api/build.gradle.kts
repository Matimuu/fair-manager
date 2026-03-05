plugins {
    id("java-library")
}

dependencies {
    implementation(project(":common-data"))
    implementation(platform(libs.bom.spring.boot))

    implementation(libs.actuator)
    implementation(libs.data.jpa)
    implementation(libs.validation)
    implementation(libs.webmvc)

    implementation(libs.flyway)
    implementation(libs.flyway.database.postgresql)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok.processor)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.actuatorTest)
    testImplementation(libs.data.jpaTest)
    testImplementation(libs.flywayTest)
    testImplementation(libs.validationTest)
    testImplementation(libs.webmvcTest)

    testImplementation(libs.testContainers.boot)
    testImplementation(libs.testContainers.junit)
    testImplementation(libs.testContainers.postgresql)

    testImplementation(libs.spring.boot.test)

    testRuntimeOnly(libs.junit.platform.launcher.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
