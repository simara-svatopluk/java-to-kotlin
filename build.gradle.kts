plugins {
    kotlin("jvm") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
//    jcenter()
}

dependencies {
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.1.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.10")
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:2.1.10")

    implementation("com.pinterest.ktlint:ktlint-core:0.47.1")
    implementation("com.pinterest.ktlint:ktlint-test:0.47.1")
    implementation("org.assertj:assertj-core:3.11.1")
//    testImplementation("com.github.shyiko.ktlint:ktlint-test:0.49.1")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}