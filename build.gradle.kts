import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    id("maven-publish")
    id("org.jetbrains.dokka") version ("1.4.20")
}
//group = "com.github.jomof"
//version = project.property("version") ?: "0.1-SNAPSHOT"
//println("---------$version------------")

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
}
dependencies {
    testImplementation(kotlin("test-junit"))
    implementation(kotlin("stdlib-jdk8"))
    //implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.1.0")
    implementation("org.apache.arrow:arrow-memory-unsafe:3.0.0")
    //implementation("org.apache.arrow:arrow-memory-netty:3.0.0")
    implementation("org.apache.arrow:arrow-memory:3.0.0")
    implementation("org.apache.arrow:arrow-vector:3.0.0")
    //testImplementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.0")
    testImplementation("org.ow2.asm:asm:9.0")
    testImplementation("org.jetbrains.lets-plot:lets-plot-common:1.5.6")
    testImplementation("org.jetbrains.lets-plot-kotlin:lets-plot-kotlin-api:1.2.0")
    testImplementation("org.jetbrains.lets-plot:lets-plot-image-export:1.5.6")
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val dokkaHtmlJar by tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-doc")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.jomof"
            artifactId = "kane"
            version = project.property("version")?.toString() ?: "0.1-SNAPSHOT"
            artifact(dokkaJavadocJar)
            artifact(dokkaHtmlJar)
            from(components["java"])
        }
    }
}
