package controllers

import DAO.BakeryDB
import models.DBTransactor
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}
import play.api.mvc._
import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future
import scala.language.postfixOps
import play.api.db.Databases
import play.api.db.Database
import play.api.db.Databases
import play.api.db.evolutions._
import org.h2.tools.Server

class APIControllerSpec
    extends PlaySpec
    with Results
    with GuiceOneAppPerTest
    with Injecting {

  val database = Databases(
    driver = "org.h2.Driver",
    url = "jdbc:h2:mem:play"
  )

  "postProduct method" should {
//    withDatabase { database =>
//      val connection = database.getConnection()
//      connection
//        .prepareStatement("insert into test values (10, 'testing')")
//        .execute()
//
//      connection
//        .prepareStatement("")
//        .executeQuery()
//        .next()

    "Return successful when adding a product with all needed values" in {
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
    "Return unsuccessful when trying to add a product with no values" in {
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
  "Edit product method" should {

    "Return successful when passing in the ID and some values" in {
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
    "Return unsuccessful when passing in an invalid ID" in {

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

    "Should return HTTP Status 200 and the current products" in {
      val controller = inject[APIController]
      val result: Future[Result] = controller
        .findAllProducts()
        .apply(FakeRequest(GET, "/rest/bakery"))
      val bodyText: String = contentAsString(result)
      status(result) mustBe 200
      bodyText must include("crepe")
    }
  }
  "Status endpoint" should {

    "Should return current service status on GET" in {
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
