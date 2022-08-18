package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers.contentAsString
import play.api.test.{FakeRequest, Injecting}

import scala.concurrent.Future
import play.api.mvc._
import play.api.test.Helpers._

import java.time.{LocalDate}

class APIControllerSpec
    extends PlaySpec
    with Results
    with GuiceOneAppPerTest
    with Injecting {

  "APIController" should {
    "Should return ok" in {
      val controller = inject[APIController]
      val result: Future[Result] = controller
        .serviceStatus()
        .apply(FakeRequest(GET, "/pass-bakery/status"))
      val bodyText: String = contentAsString(result)
      status(result) mustBe 200
      bodyText must include("pass-bakery")
      bodyText must include("Test")
      bodyText must include(LocalDate.now().toString)
    }
  }

}
