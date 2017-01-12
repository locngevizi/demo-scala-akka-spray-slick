package com.beauty

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.beauty.configs.AppConfig
import spray.can.Http
import akka.io.IO
import com.beauty.routes.ApiService
import scala.concurrent.duration.DurationLong
import akka.pattern.ask

/**
  * Created by Loc Ngo on 1/9/2017.
  */
object Main extends App {

  implicit val system = ActorSystem("beauty-platform-service")

  val api = system.actorOf(Props(new ApiService), "httpInterface")

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

  IO(Http).?(Http.Bind(listener = api, interface = AppConfig.host, port = AppConfig.port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        println(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        println("REST interface could not bind to " +
          s"${AppConfig.host}:${AppConfig.port}, ${cmd.failureMessage}")
        system.shutdown()
    }
}
