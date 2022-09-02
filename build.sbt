name := """pass-bakery"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
javaOptions += "-Dconfig.file=conf/application/dev.conf"
javaOptions in Test += "-Dconfig.file=conf/application/test.conf"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "com.dripower" %% "play-circe" % "2812.0",
  evolutions,
  guice,
  "io.circe" %% "circe-generic-extras" % "0.12.2",
  "io.circe" %% "circe-parser" % "0.12.2",
  jdbc,
  "net.postgis" % "postgis-jdbc" % "2.3.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.x" % "test",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-h2" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1"
)
