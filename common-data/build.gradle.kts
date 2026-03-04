dependencies {
    implementation(platform(libs.bom.spring.boot))

    implementation(libs.validation)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
