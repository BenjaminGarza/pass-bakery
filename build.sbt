name := """pass-bakery"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies ++= Seq(
  // more dependencies here
  "io.circe" %% "circe-generic-extras" % "0.12.2",
  "io.circe" %% "circe-parser" % "0.12.2",
  "com.dripower" %% "play-circe" % "2812.0" // compatible with Play 2.8.x
)
libraryDependencies ++= Seq(
  // Start with this one
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
  // And add any of these as needed
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1" // Postgres driver 42.3.1 + type mappings.
)
libraryDependencies ++= Seq(evolutions, jdbc)
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.x" % "test"
)
