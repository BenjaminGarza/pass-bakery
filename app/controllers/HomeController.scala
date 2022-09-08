package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject() (
    val controllerComponents: ControllerComponents,
    implicit val executionContext: ExecutionContext
) extends BaseController {

  def index(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      Future { Ok(views.html.index()) }
  }

}
