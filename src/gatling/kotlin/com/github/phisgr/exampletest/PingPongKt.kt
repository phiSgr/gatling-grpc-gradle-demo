package com.github.phisgr.exampletest

import com.github.phisgr.example.DemoServiceGrpc
import com.github.phisgr.example.Ping
import com.github.phisgr.gatling.kt.grpc.*
import com.github.phisgr.gatling.kt.javapb.MessageUpdater.Companion.updateWith
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.core.Simulation
import io.grpc.ManagedChannelBuilder
import java.util.function.Function
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class PingPongKt : Simulation() {

    private val grpcConf = grpc(ManagedChannelBuilder.forAddress("localhost", 9999).usePlaintext())
        .shareChannel()

    private val message: Function<Session, Ping> = Ping.getDefaultInstance().updateWith { it.toBuilder() }
        // dynamic payload!
        .update({ it::setData }, { it.getInt("data") })

    private fun request(name: String) = grpc(name)
        .rpc(DemoServiceGrpc.getPingPongMethod())
        .payload(message)
        .check({ extract { it.data }.isEL("#data") })

    private val scn = scenario("Play Ping Pong")
        .exec { session -> session.set("data", 0) }
        .during(10.seconds.toJavaDuration())
        .on(
            pause(500.milliseconds.toJavaDuration())
                .exec(request("Send message"))
                .exec { session -> session.set("data", 1 + session.getInt("data")) }
        )

    init {
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(grpcConf))
    }
}
