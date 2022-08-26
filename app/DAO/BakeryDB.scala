package DAO

import doobie._
import doobie.implicits._
import cats.effect.IO
import doobie.postgres.implicits._
import models.Product
import cats.effect.unsafe.implicits.global
import play.api.Configuration
import play.api.db.Database

import java.time.LocalDateTime
import java.util.UUID
import javax.inject._

class BakeryDB @Inject() (config: Configuration, db: Database) {

  val xa = Transactor.fromDriverManager[IO](
    config.get[String]("db.default.driver"),
    config.get[String]("db.default.url"),
    config.get[String]("db.default.username"),
    config.get[String]("db.default.password")
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
  ): Int = {
    val updatedAt = LocalDateTime.now()
    val sqlQuery = (name, quantity, price) match {
      case (Some(name), Some(quantity), Some(price)) =>
        sql"UPDATE product SET name = $name, quantity = $quantity, price = $price, updated_at = $updatedAt WHERE id=$id"
      case (Some(name), Some(quantity), None) =>
        sql"UPDATE product SET name = $name, quantity = $quantity, updated_at = $updatedAt WHERE id=$id"
      case (Some(name), None, None) =>
        sql"UPDATE product SET name = $name, updated_at = $updatedAt WHERE id=$id"
      case (Some(name), None, Some(price)) =>
        sql"UPDATE product SET name = $name, price = $price, updated_at = $updatedAt WHERE id=$id"
      case (None, None, None) =>
        sql""
      case (None, Some(quantity), Some(price)) =>
        sql"UPDATE product SET quantity = $quantity, price, updated_at = $updatedAt = $price WHERE id=$id"
      case (None, None, Some(price)) =>
        sql"UPDATE product SET  price = $price, updated_at = $updatedAt WHERE id=$id"
      case (None, Some(quantity), None) =>
        sql"UPDATE product SET quantity = $quantity, updated_at = $updatedAt WHERE id=$id"
    }
    sqlQuery.update.run
      .transact(xa)
      .unsafeRunSync()
  }

  def deleteByID(id: UUID): Int = {
    sql"DELETE FROM Product WHERE id=$id".update.run
      .transact(xa)
      .unsafeRunSync()
  }

}
