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

    Console.println(request.body.asText)
    val parseResult: Option[ProductFromJson] = {
      request.body.asText match {
        case None =>
          BadRequest("Request was invalid, nothing in request body")
          None
        case Some(rawText) => {
          println("Request body has text")
          circe.parser.parse(rawText) match { // Is there valid Json?
            case Left(failure) =>
              BadRequest("Request was invalid, no valid Json");
              None
            case Right(json) =>
              println(json, "Json matched")
              parser
                .decode[ProductFromJson](json.toString()) match { // Can that Json be decoded into a case class?
                case Left(error) =>
                  BadRequest(
                    "Request was invalid, json could not be parsed into case class"
                  )
                  None
                case Right(product) => Some(product)
              }
          }
        }
      }
    }
    println(parseResult, parseResult.getOrElse("Could not get parseResult"))
    parseResult
  }
  case class ProductFromJson(
      name: String,
      quantity: Int,
      price: Double
  )

  def serviceStatus(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(statusToJson)
  }

  def postProduct(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val parseResult = parseFromJson(request).getOrElse(false)
      if (parseResult.isInstanceOf[ProductFromJson]) {}
      Ok("Post successful")
  }
  def findAllProducts(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(bakeryDB.findAll().asJson.spaces2)
  }
}
