name := "beauty-platform"

version := "1.0"

scalaVersion := "2.11.6"


resolvers += "spray" at "http://repo.spray.io/"
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= {
  val akkaVersion = "2.3.11"
  val sprayVersion = "1.3.3"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-json" % "1.3.2",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "io.spray" %% "spray-testkit" % sprayVersion % "test",
    //"org.specs2" %% "specs2" % "2.3.13" % "test",

    "org.postgresql" % "postgresql" % "9.4.1208",
    //"com.typesafe.slick" %% "slick-hikaricp" % "2.1.0",
    "com.zaxxer" % "HikariCP" % "2.3.9",
    "com.typesafe.slick" %% "slick" % "2.1.0",
    "com.github.tminglei" %% "slick-pg" % "0.8.5",
    "org.slf4j" % "slf4j-nop" % "1.7.22"
  )
}

mainClass in Global := Some("com.beauty.Main")
jarName in assembly := "account-management-server.jar"
    