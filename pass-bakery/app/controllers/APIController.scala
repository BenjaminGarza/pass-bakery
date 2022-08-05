package controllers

import io.circe.generic.extras.Configuration
import io.circe.syntax.EncoderOps
import javax.inject._
import play.api._
import play.api.mvc._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Singleton
class APIController @Inject()(val environment: Environment, val controllerComponents: ControllerComponents) extends BaseController {

  def getEnvironment: String={
    environment.mode.toString
  }

  //get DateTime from server
  def getDateTime: String ={
    val dateTime: String =  LocalDateTime.now().toString
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val parsedDateTime: LocalDateTime = LocalDateTime.parse(dateTime,formatter)
    parsedDateTime.toString
  }

  //Need json format
  def getStatus: String ={
    //We need to return this info
    val status =("pass-bakery",getEnvironment,getDateTime)

    //Set implicit value for encoder parameter (Otherwise we get "could not find implicit value" error when serializing)
    implicit val customConfig: Configuration = {
      Configuration.default.withDiscriminator("type")
    }
    //Serialize to Json
    status.asJson.spaces2
  }

  def serviceStatus(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(getStatus)
  }

}
