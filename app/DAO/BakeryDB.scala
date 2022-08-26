package DAO

import doobie._
import doobie.implicits._
import cats.effect.IO
import doobie.postgres.implicits._
import models.Product
import cats.effect.unsafe.implicits.global
import java.time.{LocalDateTime}
import java.util.UUID

class BakeryDB {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5500/bakerydb",
    "postgres",
    "pass"
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

  def editByID(
      id: UUID,
      name: Option[String],
      quantity: Option[Int],
      price: Option[Double]
  ): Unit = {
    val sqlQuery = (name, quantity, price) match {
      case (Some(name), Some(quantity), Some(price)) =>
        sql"UPDATE product SET name = $name, quantity = $quantity, price = $price WHERE id=$id"
      case (Some(name), Some(quantity), None) =>
        sql"UPDATE product SET name = $name, quantity = $quantity WHERE id=$id"
      case (Some(name), None, None) =>
        sql"UPDATE product SET name = $name WHERE id=$id"
      case (Some(name), None, Some(price)) =>
        sql"UPDATE product SET name = $name, price = $price WHERE id=$id"
      case (None, None, None) =>
        sql""
      case (None, Some(quantity), Some(price)) =>
        sql"UPDATE product SET quantity = $quantity, price = $price WHERE id=$id"
      case (None, None, Some(price)) =>
        sql"UPDATE product SET  price = $price WHERE id=$id"
      case (None, Some(quantity), None) =>
        sql"UPDATE product SET quantity = $quantity WHERE id=$id"
    }
    sqlQuery.update
      .withUniqueGeneratedKeys("id", "name", "quantity", "price")
      .transact(xa)
      .unsafeRunSync()
  }

  def deleteByID(id: UUID): Int = {
    sql"DELETE FROM Product WHERE id=$id".update.run
      .transact(xa)
      .unsafeRunSync()
  }

}
