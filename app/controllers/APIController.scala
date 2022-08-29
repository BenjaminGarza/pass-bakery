package controllers

import services._
import io.circe.syntax.EncoderOps
import io.circe._

import javax.inject._
import play.api._
import play.api.mvc._
import DAO.BakeryDB
import io.circe
import models.{ProductFromJson, ServiceStatus}

import java.util.UUID

@Singleton
class APIController @Inject() (
    environment: Environment,
    val controllerComponents: ControllerComponents,
    bakeryDB: BakeryDB
) extends BaseController {

  def statusToJson: String = {
    val status = ServiceStatus(
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
          None
        case Some(rawText) => {
          circe.parser.parse(rawText) match {
            case Left(failure) =>
              None
            case Right(json) =>
              parser
                .decode[ProductFromJson](json.toString()) match {
                case Left(error) =>
                  None
                case Right(product) => Some(product)
              }
          }
        }
      }
    }
    parseResult
  }

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
          product.name match {
            case None => BadRequest("Post failed on name")
            case Some(name) =>
              product.quantity match {
                case None => BadRequest("Post failed on quantity")
                case Some(quantity) =>
                  product.price match {
                    case None => BadRequest("Post failed on price")
                    case Some(price) =>
                      val rowsUpdated =
                        bakeryDB.addProduct(
                          name,
                          quantity,
                          price
                        )
                      Ok(
                        "Post successful, " ++ rowsUpdated.toString ++ " rows updated"
                      )
                  }
              }
          }
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
              Ok(rowsModified.toString)
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
