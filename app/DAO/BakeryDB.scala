package DAO

import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import cats.effect.IO
import doobie.util.ExecutionContexts
import cats.implicits
import cats._
import cats.data._
import cats.effect._
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

  def addProduct(name: String, quantity: Int, price: Double): Update0 = {
//    val uuid = gen_random_uuid()
    val insertStatement =
      sql"INSERT INTO Product (id, name, quantity, price, createdAt, updatedAt) VALUES (gen_random_uuid(), $name, $quantity, $price)"
    Console.println(insertStatement, "Insert statement")
    insertStatement.update

  }

  def findAll(): List[Product] = {
    sql"SELECT * FROM Product"
      .query[Product]
      .to[List]
      .transact(xa)
      .unsafeRunSync()
  }

}
