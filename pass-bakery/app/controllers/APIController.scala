package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import io.circe.generic.extras.auto._
import io.circe.syntax._
import io.circe.parser
import io.circe.generic.extras.Configuration
import io.circe

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class APIController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
    //Define circe response, need to move out of controller when finished
  case class Status(service: "pass-bakery", environment: String, serverTime: String)
  val status = Status("pass-bakery","evironment variable", "server time variable")

  implicit val customConfig: Configuration = {
    Configuration.default.withDiscriminator("type")
  }
  val serializedStatus: String = status.asJson.spaces2
  def echo() = Action { request =>
    Ok(serializedStatus)
  }
}
