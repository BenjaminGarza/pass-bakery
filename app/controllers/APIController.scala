package controllers

import services._
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.syntax.EncoderOps
import io.circe._

import javax.inject._
import play.api._
import play.api.mvc._
import DAO.BakeryDB
import io.circe

import java.util.UUID

@Singleton
class APIController @Inject() (
    environment: Environment,
    val controllerComponents: ControllerComponents,
    bakeryDB: BakeryDB
) extends BaseController {

  def statusToJson: String = {
    val status = models.Status(
      "pass-bakery",
      environment.mode.toString,
      APIService.getDateTime
    )
    status.asJson.spaces2
  }

  def parseFromJson(request: Request[AnyContent]): Option[ProductFromJson] = {
    val parseResult: Option[ProductFromJson] = {
      request.body.asText match {
        case None =>
          println("asText failed")
          None
        case Some(rawText) => {
          println(rawText)
          circe.parser.parse(rawText) match {
            case Left(failure) =>
              println("Parser failed")
              None
            case Right(json) =>
              println(json.toString())
              parser
                .decode[ProductFromJson](json.toString()) match {
                case Left(error) =>
                  println(error)
                  None
                case Right(product) => Some(product)
              }
          }
        }
      }
    }
    parseResult
  }
  case class ProductFromJson(
      name: Option[String],
      quantity: Option[Int],
      price: Option[Double]
  )

  def serviceStatus(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(statusToJson)
  }

  def postProduct(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      parseFromJson(request) match {
        case None =>
          BadRequest("Post failed")
        case Some(product) =>
          val rowsUpdated =
            bakeryDB.addProduct(
              product.name.get,
              product.quantity.get,
              product.price.get
            ) //Need to properly setup getOrElse here
          Ok("Post successful, " ++ rowsUpdated.toString ++ " rows updated")
          Ok("Work in progress")
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
  def editByID(id: UUID): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      bakeryDB.findByID(id) match {
        case None =>
          NotFound("Product not found")
        case Some(product) =>
          parseFromJson(request) match {
            case None =>
              BadRequest("Update failed")
            case Some(product) =>
              val rowsModified = bakeryDB.editByID(
                id,
                product.name,
                product.quantity,
                product.price
              )
              Ok("")
          }
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
