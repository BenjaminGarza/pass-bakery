package models

import cats.effect.IO
import doobie.Transactor
import play.api.{Configuration, Environment}
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
