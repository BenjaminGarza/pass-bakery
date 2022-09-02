package controllers

import akka.stream.Materializer
import akka.util.ByteString
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Play.materializer
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest, Injecting}
import play.api.mvc._
import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future
import scala.language.postfixOps
import play.api.db.Databases
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.streams.Accumulator

class APIControllerSpec
    extends PlaySpec
    with Results
    with GuiceOneAppPerTest
    with Injecting {

  def mockApp = new GuiceApplicationBuilder().build()
  val mtrlzr = mockApp.injector.instanceOf[Materializer]

  val database = Databases(
    driver = "org.h2.Driver",
    url = "jdbc:h2:mem:play"
  )

  "postProduct method" should {
    "return successful when adding a product with all needed values" in {
      val controller = inject[APIController]
      val result: Accumulator[ByteString, Result] = controller
        .postProduct()
        .apply(
          FakeRequest(
            POST,
            "/pass-bakery/product"
          ).withBody("""{"name": "crepe", "quantity": 5, "price": 4.99}""")
            .withHeaders(("Content-Type:", "application/json"))
        )
      val runResult: Future[Result] = result.run()(mtrlzr)

      status(runResult) mustBe 200
    }
    "return unsuccessful when trying to add a product with no values" in {
      val controller = inject[APIController]
      val result: Accumulator[ByteString, Result] = controller
        .postProduct()
        .apply(
          FakeRequest(
            POST,
            "/pass-bakery/product"
          ).withBody("""""")
            .withHeaders(("Content-Type:", "application/json"))
        )
      val runResult: Future[Result] = result.run()(mtrlzr)
      status(runResult) mustBe 400
    }
  }
  "Edit product method" should {

    "return successful when passing in the ID and some values" in {
      val controller = inject[APIController]
      val uuid: UUID = UUID.fromString("a62bf2f7-732d-47a0-b791-3140784784b0")

      val result: Accumulator[ByteString, Result] = controller
        .editByID(uuid)
        .apply(
          FakeRequest(
            PUT,
            "/pass-bakery/product"
          ).withBody("""{"name": "crepe", "quantity": 5, "price": 4.99}""")
            .withHeaders(("Content-Type:", "application/json"))
        )
      val runResult: Future[Result] = result.run()(mtrlzr)

      status(runResult) mustBe 200
    }
    "return unsuccessful when passing in an invalid ID" in {

      val controller = inject[APIController]
      val uuid: UUID = UUID.fromString("a62bf2f7-732d-47a0-b791-3140784704b0")

      val result = controller
        .editByID(uuid)
        .apply(
          FakeRequest(
            POST,
            "/pass-bakery/product"
          ).withBody("""{"name": "crepe", "quantity": 5, "price": 4.99}""")
            .withHeaders(("Content-Type:", "application/json"))
        )
      val runResult: Future[Result] = result.run()(mtrlzr)
      status(runResult) mustBe 400
    }
  }
  "findAllProducts" should {

    "return HTTP Status 200 when retrieving all products" in {
      val controller = inject[APIController]
      val result: Future[Result] = controller
        .findAllProducts()
        .apply(FakeRequest(GET, "/rest/bakery"))
      val bodyText: String = contentAsString(result)
      status(result) mustBe 200
    }
  }
  "Status endpoint" should {

    "return current service status on GET" in {
      val controller = inject[APIController]
      val result: Future[Result] = controller
        .serviceStatus()
        .apply(FakeRequest(GET, "/pass-bakery/status"))
      val bodyText: String = contentAsString(result)
      status(result) mustBe 200
    }
  }
//  "Delete product" should {
//
//    "Return success when deleting a product from the database" in {
//      val controller = inject[APIController]
//      val name = "l@at&te9008865"
//      val setUp: Future[Result] = controller
//        .postProduct()
//        .apply(
//          FakeRequest(
//            POST,
//            "/pass-bakery/product"
//          ).withTextBody(
//            text =
//              "{\"name\": \"l@at&te9008865\", \"quantity\": \"9\", \"price\": \"8.99\"}"
//          )
//        )
//      val findByName = bakeryDB.findByName(name)
//      val result = findByName match {
//        case None => "Nothing here"
//        case Some(product) =>
//          controller.deleteByID(product.id).toString()
//      }
//      result mustBe 200
//
//    }
//  }
  // stop the H2 DB TCP Server
  database.shutdown()
}
