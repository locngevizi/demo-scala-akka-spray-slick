package com.quiz.management

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration.DurationLong

/**
  * Created by Loc Ngo on 12/29/2016.
  */
object Main  extends App {
  implicit val system = ActorSystem("quiz-management-service")

  val api = system.actorOf(Props(new RestInterface), "httpInterface")

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

trait AppConfig {
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  val dbConf = config.getConfig("database")

  val dbHost = dbConf.getString("host")
  val dbPort = dbConf.getInt("port")
  val dbName = dbConf.getString("name")
  val dbUser = dbConf.getString("user")
  val dbPassword = dbConf.getString("password")
  val dbTimeOut = dbConf.getInt("timeOut")
  val dbPoolSize = dbConf.getInt("poolSize")
}

object AppConfig extends AppConfig
