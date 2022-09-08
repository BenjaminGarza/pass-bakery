package DAO

import cats.effect.unsafe.implicits.global
import doobie.implicits._
import doobie.postgres.implicits._
import models.DBTransactor
import models.Product
import play.api.Configuration
import play.api.db.Database
import java.time.LocalDateTime
import java.util.UUID
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

class BakeryDB @Inject() (
    config: Configuration,
    db: Database,
    dbTransactor: DBTransactor,
    implicit val executionContext: ExecutionContext
) {
  val xa = dbTransactor.xa

  def addProduct(
      name: String,
      quantity: Int,
      price: Double
  ): Future[Int] = {
    val createdAt = LocalDateTime.now()
    val insertStatement = {
      sql"INSERT INTO product (id, name, quantity, price, created_at, updated_at) VALUES (gen_random_uuid(), $name, $quantity, $price, $createdAt, $createdAt)"
    }

    insertStatement.update.run
      .transact(xa)
      .unsafeToFuture()
  }

  def addProductWithUUID(
      name: String,
      quantity: Int,
      price: Double,
      uuid: UUID
  ): Future[Int] = {
    val createdAt = LocalDateTime.now()
    val insertStatement = {
      sql"INSERT INTO product (id, name, quantity, price, created_at, updated_at) VALUES ($uuid, $name, $quantity, $price, $createdAt, $createdAt)"
    }

    insertStatement.update.run
      .transact(xa)
      .unsafeToFuture()
  }

  def findByID(id: UUID): Future[Option[Product]] = {
    sql"SELECT * FROM Product WHERE id=$id"
      .query[Product]
      .option
      .transact(xa)
      .unsafeToFuture()
  }

  def findByName(name: String): Future[Option[Product]] = {
    sql"SELECT * FROM Product WHERE name=$name"
      .query[Product]
      .option
      .transact(xa)
      .unsafeToFuture()
  }

  def findAll(): Future[List[Product]] = {
    sql"SELECT * FROM Product"
      .query[Product]
      .to[List]
      .transact(xa)
      .unsafeToFuture()
  }

  def editByID(
      id: UUID,
      name: Option[String],
      quantity: Option[Int],
      price: Option[Double]
  ): Future[Int] = {
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
      .unsafeToFuture()
  }

  def deleteByID(id: UUID): Future[Int] = {
    sql"DELETE FROM Product WHERE id=$id".update.run
      .transact(xa)
      .unsafeToFuture()
  }

}
