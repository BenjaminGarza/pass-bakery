package DAO

import doobie._
import doobie.implicits._
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import play.api.libs.json.{Json, Writes}

class BakeryDB {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5500/bakerydb",
    "postgres",
    "pass"
  )

  case class Products(
      id: String,
      name: String,
      quantity: Int,
      price: Double,
      createdAt: String,
      updatedAt: String
  )

  object Products {
    implicit val writes: Writes[Products] = Json.writes[Products]
  }

  def findAll(): List[String] =
    sql"select * from products"
      .query[String]
      .to[List]
      .transact(xa)
      .unsafeRunSync()

}
