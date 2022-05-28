import com.google.protobuf.gradle.*

plugins {
    java
    idea

    kotlin("jvm") version "1.6.20"

    application

    id("com.google.protobuf") version "0.8.14"
    id("io.gatling.gradle") version "3.7.6.3"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.20.1")
    implementation("io.grpc:grpc-netty-shaded:1.46.0")
    implementation("io.grpc:grpc-protobuf:1.46.0")
    implementation("io.grpc:grpc-stub:1.46.0")

    implementation("javax.annotation:javax.annotation-api:1.3.2")

    gatling("com.github.phisgr:gatling-javapb:1.2.0")
    gatling("com.github.phisgr:gatling-grpc:0.13.0")
    gatling("com.github.phisgr:kt:0.1.0-SNAPSHOT")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.34.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

application {
    mainClassName = "com.github.phisgr.example.DemoServer"
}
