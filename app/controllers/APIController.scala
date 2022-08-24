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

  def parseFromJson(request: Request[AnyContent]): Any = {

    Console.println(request.body.asText)
    val parseResult =
      request.body.asText match {

        case None =>
          BadRequest("Request was invalid, json could not be parsed");
          println("Request body has no text")

        case Some(rawText) => {
          println("Request body has text")
          circe.parser.parse(rawText) match {
            case Left(failure) =>
              BadRequest("Request was invalid, json could not be parsed");
              println("Json couldn't be parsed")
            case Right(json) =>
              println("Pattern matched ")
              println(json, "Line 60")
              val product = parser
                .decode[ProductFromJson](json.toString())
              product match {
                case Left(error)    =>
                case Right(product) => println(product.name, "Product")
              }

            //          bakeryDB.addProduct(
            //            name,
            //            quantity,
            //            price
            //          )
          }
        }

      }

    println(parseResult.toString, "the parse result")

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
      parseFromJson(request)
      Ok("Post successful")
  }
  def findAllProducts(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(bakeryDB.findAll().asJson.spaces2)
  }
}
