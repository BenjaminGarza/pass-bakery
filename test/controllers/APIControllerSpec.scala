package controllers

import DAO.BakeryDB
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}
import play.api.mvc._
import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future
import scala.language.postfixOps

class APIControllerSpec(bakeryDB: BakeryDB)
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
            "/pass-bakery/product"
          ).withTextBody(
            text =
              "{\"name\": \"crepe\", \"quantity\": \"5\", \"price\": \"4.99\"}"
          )
        )

      val bodyText: String = contentAsString(result)
      bodyText mustBe ("Post successful, 1 rows updated")
    }
    "Return unsuccessful" in {
      intercept[Exception] {
        val controller = inject[APIController]
        val result: Future[Result] = controller
          .postProduct()
          .apply(
            FakeRequest(
              POST,
              "/pass-bakery/product"
            ).withTextBody(
              text = "{}"
            )
          )
      }
    }
  }
  "Edit product" should {
    "Return successful" in {
      val controller = inject[APIController]
      val uuid: UUID = UUID.fromString("a62bf2f7-732d-47a0-b791-3140784784b0")
      val result: Future[Result] = controller
        .editByID(uuid)
        .apply(
          FakeRequest(
            PUT,
            " http://localhost:9000/rest/bakery/product/a62bf2f7-732d-47a0-b791-3140784784b0"
          ).withTextBody(
            text = "{\"name\": \"crepe\", \"price\": \"9.99\"}"
          )
        )

      val bodyText: String = contentAsString(result)
      bodyText mustBe ("1")
    }
    "Return unsuccessful" in {

      val controller = inject[APIController]
      val uuid: UUID = UUID.fromString("a62bf2f7-732d-47a0-b791-3140784704b0")
      val result: Future[Result] = controller
        .editByID(uuid)
        .apply(
          FakeRequest(
            PUT,
            "/pass-bakery/product"
          ).withTextBody(
            text = "{\"name\": \"crepe\", \"price\": \"9.99\"}"
          )
        )
      val bodyText: String = contentAsString(result)
      bodyText mustBe ("Product not found")
    }
  }
  "findAllProducts" should {
    "Should return success" in {
      val controller = inject[APIController]
      val result: Future[Result] = controller
        .findAllProducts()
        .apply(FakeRequest(GET, "/rest/bakery"))
      val bodyText: String = contentAsString(result)
      status(result) mustBe 200
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
  "Delete product" should {
    "Return successful" in {
      val controller = inject[APIController]
      val name = "l@at&te9008865"
      val setUp: Future[Result] = controller
        .postProduct()
        .apply(
          FakeRequest(
            POST,
            "/pass-bakery/product"
          ).withTextBody(
            text =
              "{\"name\": \"l@at&te9008865\", \"quantity\": \"9\", \"price\": \"8.99\"}"
          )
        )
      val findByName = bakeryDB.findByName(name)
      val result = findByName match {
        case None => "Nothing here"
        case Some(product) =>
          controller.deleteByID(product.id).toString()
      }
      result must include("1 row deleted")

    }
  }
}
