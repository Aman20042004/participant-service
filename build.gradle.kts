plugins {
	id("org.springframework.boot") version "3.5.16"
	id("io.spring.dependency-management") version "1.1.7"

	kotlin("jvm") version "2.1.10"
	kotlin("plugin.spring") version "2.1.10"
}

group = "com.participant"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.jetbrains.kotlin:kotlin-reflect")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}