package com.github.phisgr.exampletest

import com.github.phisgr.example.DemoServiceGrpc
import com.github.phisgr.example.Ping
import com.github.phisgr.gatling.kt.grpc.*
import com.github.phisgr.gatling.kt.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.*
import io.grpc.ManagedChannelBuilder
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class PingPongKt : Simulation() {

    private val grpcConf = grpc(ManagedChannelBuilder.forAddress("localhost", 9999).usePlaintext())
        .shareChannel()

    private fun request(name: String) = grpc(name)
        .rpc(DemoServiceGrpc.getPingPongMethod())
        .payload(Ping::newBuilder) { session ->
            // dynamic payload!
            data = session.getInt("data")
            build()
        }
        .check({ extract { it.data }.isEL("#{data}") })

    private val scn = scenario("Play Ping Pong") {
        +hook { it.set("data", 0) }
        +during(10.seconds.toJavaDuration()).on {
            +pause(500.milliseconds.toJavaDuration())
            +request("Send message")
            +hook { session -> session.set("data", 1 + session.getInt("data")) }
        }
    }

    init {
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(grpcConf))
    }
}
