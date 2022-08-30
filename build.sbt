name := """pass-bakery"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "io.circe" %% "circe-generic-extras" % "0.12.2",
  "io.circe" %% "circe-parser" % "0.12.2",
  "com.dripower" %% "play-circe" % "2812.0",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.x" % "test",
  "net.postgis" % "postgis-jdbc" % "2.3.0",
  evolutions,
  jdbc,
  jdbc % Test
)
