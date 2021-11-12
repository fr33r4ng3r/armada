import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.kordamp.gradle.jdeps") version "0.15.0"
    id("org.beryx.jlink") version "2.24.2"
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2-native-mt")
    implementation("com.jfoenix:jfoenix:9.0.10")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.5.6")
    implementation("org.springframework.boot:spring-boot-starter-rsocket:2.5.6")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2-native-mt")
}

val tag: String = System.getenv().getOrDefault("TAG", "0.1")

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    filesMatching("**/version.properties") {
        expand("version" to tag)
    }
}

val compileKotlin: KotlinCompile by tasks
val compileJava: JavaCompile by tasks
sourceSets {
    main {
        compileKotlin.destinationDirectory.set(compileJava.destinationDirectory)
        output.setResourcesDir(compileJava.destinationDirectory)
    }
    test {
        compileKotlin.destinationDirectory.set(compileJava.destinationDirectory)
        output.setResourcesDir(compileJava.destinationDirectory)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
    modularity.inferModulePath.set(true)
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(16))
    }
}

javafx {
    version = "16"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.swing", "javafx.base")
}

application {
    mainModule.set("armada.main")
    mainClass.set("armada.Main")
}

jlink {
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    addExtraDependencies("javafx")
    mergedModule {
        additive = true
        uses("ch.qos.logback.classic.spi.Configurator")
        excludeRequires("com.fasterxml.jackson.module.paramnames")
        excludeProvides(mapOf("implementation" to "com.sun.xml.bind.v2.ContextFactory"))
        excludeProvides(mapOf("servicePattern" to "javax.enterprise.inject.*"))
        excludeProvides(mapOf("service" to "org.apache.logging.log4j.spi.Provider"))
        excludeProvides(mapOf("service" to "reactor.blockhound.integration.BlockHoundIntegration"))
        excludeProvides(mapOf("service" to "org.apache.logging.log4j.util.PropertySource"))
        excludeProvides(mapOf("service" to "javax.servlet.ServletContainerInitializer"))
    }
    launcher {
        name = "armada"
        version = tag
        setJvmArgs(listOf(
            "--add-reads", "armada.merged.module=kotlin.reflect",
            "--add-reads", "armada.merged.module=armada.main"
        ))
    }
    jpackage {
        imageOptions.addAll(arrayOf("--resource-dir", "${projectDir}\\jpackage", "--verbose"))
        installerOptions.addAll(
            arrayOf(
                "--win-per-user-install",
                "--win-dir-chooser",
                "--type",
                "msi",
                "--win-shortcut",
                "--win-menu",
                "--win-menu-group",
                "Armada"
            )
        )
    }
}
