import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    `build-scan`
    id("org.jetbrains.kotlin.jvm").version("1.3.41")
}

group = "pt.br.lib"
version = "0.0.1"

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.oath.halodb:halodb:0.5.3")
    implementation("com.uchuhimo:konf-yaml:0.20.0")
    implementation("com.uchuhimo:konf-core:0.20.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.amshove.kluent:kluent:1.53")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
    withType<Delete> {
        delete("out")
    }
}

