package models

import cats.effect.IO
import doobie.Transactor
import play.api.{Configuration, Environment}
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.h2._

import javax.inject.Inject

class DBTransactor @Inject() (environment: Environment, config: Configuration) {
  val xa: Transactor[IO] =
    Transactor.fromDriverManager[IO](
      config.get[String]("db.default.driver"),
      config.get[String]("db.default.url"),
      config.get[String]("db.default.username"),
      config.get[String]("db.default.password")
    )
}
