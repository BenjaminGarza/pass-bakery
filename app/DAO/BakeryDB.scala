package DAO

import doobie._
import doobie.implicits._
import cats.effect.IO
import cats.effect.unsafe.implicits.global

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
      createdAt: String,
      updatedAt: String
  )

  def findAll(): List[Product] =
    sql"select * from products"
      .query[Product]
      .to[List]
      .transact(xa)
      .unsafeRunSync()

}
