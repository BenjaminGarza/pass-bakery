package controllers

import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}
import play.api.mvc._
import java.time.LocalDate
import scala.concurrent.Future
import scala.language.postfixOps

class APIControllerSpec
    extends PlaySpec
    with Results
    with GuiceOneAppPerTest
    with Injecting {
  "Create product" should {
    "Return successful" in {
      val controller = inject[APIController]
      val result: Future[Result] = controller
        .postProduct()
        .apply(
          FakeRequest(
            POST,
            "/pass-bakery/status"
          ).withTextBody(
            text =
              "{\"name\": \"crepe\", \"quantity\": \"5\", \"price\": \"4.99\"}"
          )
        )
      val bodyText: String = contentAsString(result)
      bodyText mustBe ("Post successful, 1 rows updated")
    }
  }
  "findAllProducts" should {
    "Should return Nil when table is empty" in {
      val controller = inject[APIController]
      val result: Future[Result] = controller
        .findAllProducts()
        .apply(FakeRequest(GET, "/rest/bakery"))
      val bodyText: String = contentAsString(result)
      status(result) mustBe 200
      bodyText must include("Nil")
    }
  }
  "Status endpoint" should {
    "Should return status" in {
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
