package controllers

import akka.actor.ActorSystem
import javax.inject._
import play.api._
import play.api.libs.concurrent.CustomExecutionContext
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

trait HomeControllerExecutionContext extends ExecutionContext

class HomeControllerExecutionContextImpl @Inject() (system: ActorSystem)
    extends CustomExecutionContext(system, "my.executor")
    with HomeControllerExecutionContext

@Singleton
class HomeController @Inject() (
    val controllerComponents: ControllerComponents,
    implicit val homeControllerExecutionContext: HomeControllerExecutionContext
) extends BaseController {

  def index(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      Future { Ok(views.html.index()) }
  }

}
