package controllers

import DAO.BakeryDB
import io.circe.syntax.EncoderOps
import play.api.libs.circe.Circe
import play.api._
import play.api.mvc._
import models.{ProductFromJson, ServiceStatus}
import services._
import java.util.UUID
import javax.inject._

@Singleton
class APIController @Inject() (
    environment: Environment,
    val controllerComponents: ControllerComponents,
    bakeryDB: BakeryDB
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

  def serviceStatus(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(statusToJson)
  }

  def postProduct() = Action(circe.tolerantJson[ProductFromJson]) { request =>
    val product = request.body
    (product.name, product.quantity, product.price) match {
      case (Some(name), Some(quantity), Some(price)) =>
        val rowsUpdated =
          bakeryDB.addProduct(
            name,
            quantity,
            price
          )
        Ok(
          "Post successful, " ++ rowsUpdated.toString ++ " rows updated"
        )
      case noMatch => BadRequest("Post failed")

    }
  }

  def findByID(id: UUID): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      bakeryDB.findByID(id) match {
        case None =>
          NotFound("Product not found")
        case Some(product) =>
          Ok(product.asJson.spaces2)
      }
  }
  def findAllProducts(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(bakeryDB.findAll().asJson.spaces2)
  }
  def editByID(id: UUID): Action[ProductFromJson] =
    Action(circe.tolerantJson[ProductFromJson]) { request =>
      bakeryDB.findByID(id) match {
        case None =>
          NotFound("Product not found")
        case Some(foundProduct) =>
          val requestProduct = request.body
          val rowsModified = bakeryDB.editByID(
            id,
            requestProduct.name,
            requestProduct.quantity,
            requestProduct.price
          )
          Ok(rowsModified.toString)
      }
    }
  def deleteByID(id: UUID): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      bakeryDB.findByID(id) match {
        case None =>
          NotFound("Product not found")
        case Some(product) =>
          val rowsModified = bakeryDB.deleteByID(id)
          Ok(rowsModified.toString ++ " row deleted")
      }
  }
}
