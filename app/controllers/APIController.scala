package controllers

import services._
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.syntax.EncoderOps
import javax.inject._
import play.api._
import play.api.mvc._
import DAO.BakeryDB

@Singleton
class APIController @Inject() (
    val environment: Environment,
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

  def serviceStatus(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(statusToJson)
  }

  def findAllProducts(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(bakeryDB.findAll().asJson.spaces2)
  }
}
