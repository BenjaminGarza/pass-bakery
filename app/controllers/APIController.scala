package controllers

import DAO.BakeryDB
import io.circe.syntax.EncoderOps
import java.util.UUID
import javax.inject._
import models.{ProductFromJson, ServiceStatus}
import play.api.libs.circe.Circe
import play.api._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import services._

@Singleton
class APIController @Inject() (
    environment: Environment,
    val controllerComponents: ControllerComponents,
    bakeryDB: BakeryDB,
    implicit val executionContext: ExecutionContext
) extends BaseController
    with Circe {

  def statusToJson: String = {
    val status = ServiceStatus(
      "pass-bakery",
      environment.mode.toString,
      APIService.getDateTime
    )
    status.asJson.spaces2
  }

  def serviceStatus(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      Future { Ok(statusToJson) }
  }

  def postProduct(): Action[ProductFromJson] =
    Action.async(circe.tolerantJson[ProductFromJson]) { request =>
      Future {
        val product = request.body
        (product.name, product.quantity, product.price, product.uuid) match {
          case (Some(name), Some(quantity), Some(price), None) =>
            bakeryDB.addProduct(
              name,
              quantity,
              price
            )
            Ok("Post successful")
          case (Some(name), Some(quantity), Some(price), Some(uuid)) =>
            bakeryDB.addProductWithUUID(
              name,
              quantity,
              price,
              uuid
            )
            Ok("Post with uuid successful")
          case noMatch => BadRequest("Post failed")
        }
      }
    }

  def findByID(id: UUID): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      bakeryDB.findByID(id).map {
        case None =>
          NotFound("Product not found")
        case Some(product) =>
          Ok(product.asJson.spaces2)
      }
  }

  def findAllProducts(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      bakeryDB
        .findAll()
        .map { products =>
          Ok(products.asJson.spaces2)
        }
  }

  def editByID(id: UUID): Action[ProductFromJson] =
    Action.async(circe.tolerantJson[ProductFromJson]) { request =>
      bakeryDB.findByID(id).map {
        case Some(foundProduct) => {
          val requestProduct = request.body
          val rowsModified = bakeryDB.editByID(
            id,
            requestProduct.name,
            requestProduct.quantity,
            requestProduct.price
          )
          Ok(rowsModified.toString)
        }
        case None =>
          NotFound("Product not found")
      }
    }

  def deleteByID(id: UUID): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      bakeryDB.findByID(id).map {
        case None =>
          NotFound("Product not found")
        case Some(product) =>
          bakeryDB.deleteByID(id)
          Ok("Product deleted")
      }
  }
}
