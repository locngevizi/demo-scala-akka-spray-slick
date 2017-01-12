package com.beauty.configs

import com.typesafe.config.ConfigFactory

/**
  * Created by Loc Ngo on 1/9/2017.
  */
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