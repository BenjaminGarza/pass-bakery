package controllers

import models._
import javax.inject._
import play.api._
import play.api.mvc._


@Singleton
class APIController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def echo() = Action { request =>
    Ok(models.Status.getStatus)
  }
}
