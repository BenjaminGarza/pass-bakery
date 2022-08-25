package DAO

import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import cats.effect.IO
import doobie.util.ExecutionContexts
import cats.implicits
import doobie.postgres.implicits._
import doobie.postgres.pgisimplicits._
import cats.effect.unsafe.implicits.global

import java.time.{LocalDateTime, OffsetDateTime}
import java.util.UUID

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

  def addProduct(
      name: String,
      quantity: Int,
      price: Double
  ): Int = {
    val createdAt = LocalDateTime.now()
    val insertStatement = {
      sql"INSERT INTO product (id, name, quantity, price, created_at, updated_at) VALUES (gen_random_uuid(), $name, $quantity, $price, $createdAt, $createdAt)"
    }

    insertStatement.update.run
      .transact(xa)
      .unsafeRunSync()
  }

  def findByID(id: UUID): Option[Product] = {
    sql"SELECT * FROM Product WHERE id=$id"
      .query[Product]
      .option
      .transact(xa)
      .unsafeRunSync()
  }

  def findAll(): List[Product] = {
    sql"SELECT * FROM Product"
      .query[Product]
      .to[List]
      .transact(xa)
      .unsafeRunSync()
  }

}
