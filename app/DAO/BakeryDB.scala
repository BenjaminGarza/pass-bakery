package DAO

import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import cats.effect.IO
import doobie.postgres.implicits._
import doobie.postgres.pgisimplicits._
import cats.effect.unsafe.implicits.global
import java.time.{LocalDateTime, OffsetDateTime}

class BakeryDB {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5500/bakerydb",
    "postgres",
    "pass"
  )

  case class Product(
      id: String,
      name: String,
      quantity: Int,
      price: Double,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime
  )

  def findAll(): List[Product] = {
    sql"select * from Product"
      .query[Product]
      .to[List]
      .transact(xa)
      .unsafeRunSync()
  }

}
