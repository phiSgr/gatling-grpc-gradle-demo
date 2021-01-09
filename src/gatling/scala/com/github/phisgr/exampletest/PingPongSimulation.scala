package com.github.phisgr.exampletest

import com.github.phisgr.example.{DemoServiceGrpc, Ping}
import com.github.phisgr.gatling.grpc.Predef._
import com.github.phisgr.gatling.javapb._
import io.gatling.core.Predef._
import io.gatling.core.session.Expression

import scala.concurrent.duration._

class PingPongSimulation extends Simulation {

  val grpcConf = grpc(managedChannelBuilder("localhost", 9999).usePlaintext())
    .shareChannel

  val message: Expression[Ping] =
    Ping.getDefaultInstance
      // dynamic payload!
      .update(_.setData)($("data"))

  def request(name: String) = grpc(name)
    .rpc(DemoServiceGrpc.getPingPongMethod)
    .payload(message)
    .extract(_.getData.some)(_ is $("data"))

  val scn = scenario("Play Ping Pong")
    .exec(_.set("data", 0))
    .during(10.second) {
      pause(500.millis)
        .exec(request("Send message"))
        .exec(session => session.set("data", 1 + session("data").as[Int]))
    }

  setUp(scn.inject(atOnceUsers(1)).protocols(grpcConf))
}
