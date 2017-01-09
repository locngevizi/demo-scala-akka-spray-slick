package com.quiz.management

/**
  * Created by Loc Ngo on 1/4/2017.
  */
import com.quiz.management.DarkroomDriver.simple._
import com.zaxxer.hikari.HikariDataSource

object Persistence {
  lazy val db = {
    val jdbcUrl = s"jdbc:postgresql://${AppConfig.dbHost}:${AppConfig.dbPort}/${AppConfig.dbName}"

    val ds = new HikariDataSource

    ds.setJdbcUrl(jdbcUrl)
    ds.setPoolName(s"${AppConfig.dbName}-persistence-pool")
    // We need to set the driver classname manually per the recommendation in the
    // HikariCP documentation (https://github.com/brettwooldridge/HikariCP#essentials)
    // to work around an issue with driver lookup failing after a database reset.
    ds.setDriverClassName("org.postgresql.Driver")
    ds.setUsername(AppConfig.dbUser)
    ds.setPassword(AppConfig.dbPassword)
    ds.setMaximumPoolSize(AppConfig.dbPoolSize)
    ds.setConnectionTimeout(AppConfig.dbTimeOut)

    Database.forDataSource(ds)
  }
}
