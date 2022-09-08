package controllers

import java.util.UUID
import models.ProductFromJson
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.db.Databases
import play.api.mvc._
import play.api.Play.materializer
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest, Injecting}
import play.api.inject.guice.GuiceApplicationBuilder
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class APIControllerSpec
    extends PlaySpec
    with Results
    with GuiceOneAppPerTest
    with Injecting {

  def mockApp = new GuiceApplicationBuilder().build()

  val database = Databases(
    driver = "org.h2.Driver",
    url = "jdbc:h2:mem:play"
  )

  "postProduct method" should {
    "return successful when adding a product with all needed values" in {
      val controller = inject[APIController]
      val product = ProductFromJson(Some("crepe"), Some(5), Some(4.99), None)
      val fakeRequest = FakeRequest(
        POST,
        controllers.routes.APIController.postProduct().url
      ).withBody(product)
        .withHeaders(("Content-Type" -> "application/json"))

      val result: Future[Result] = controller
        .postProduct()
        .apply(
          fakeRequest
        )

      status(result) mustBe 200
    }
    "return unsuccessful when trying to add a product with no values" in {
      val controller = inject[APIController]
      val product = ProductFromJson(None, None, None, None)
      val fakeRequest = FakeRequest(
        POST,
        controllers.routes.APIController.postProduct().url
      ).withBody(product)
        .withHeaders(("Content-Type" -> "application/json"))

      val result: Future[Result] = controller
        .postProduct()
        .apply(
          fakeRequest
        )

      status(result) mustBe 400
    }
    "return unsuccessful when trying to add a product with some values" in {
      val controller = inject[APIController]
      val product = ProductFromJson(Some("crepe"), None, Some(4.99), None)
      val fakeRequest = FakeRequest(
        POST,
        controllers.routes.APIController.postProduct().url
      ).withBody(product)
        .withHeaders(("Content-Type" -> "application/json"))

      val result: Future[Result] = controller
        .postProduct()
        .apply(
          fakeRequest
        )

      status(result) mustBe 400
    }
  }
  "Edit product method" should {
    "return successful when passing in the ID and some values" in {
      val controller = inject[APIController]
      val uuid: UUID = UUID.randomUUID()
      val product =
        ProductFromJson(Some("crepe"), Some(5), Some(4.99), Some(uuid))
      val fakePostRequest = FakeRequest(
        POST,
        controllers.routes.APIController.postProduct().url
      ).withBody(product)
        .withHeaders(("Content-Type" -> "application/json"))

      controller
        .postProduct()
        .apply(
          fakePostRequest
        )

      val editProduct = ProductFromJson(None, Some(10), Some(9.99), None)
      val fakeEditRequest = FakeRequest(
        PUT,
        controllers.routes.APIController.editByID(uuid).url
      ).withBody(editProduct)
        .withHeaders(("Content-Type" -> "application/json"))

      val result: Future[Result] = controller
        .editByID(uuid)
        .apply(
          fakeEditRequest
        )

      status(result) mustBe 200
    }
    "return unsuccessful when passing in an invalid ID" in {

      val controller = inject[APIController]
      val uuid: UUID = UUID.randomUUID()

      val result = controller
        .editByID(uuid)
        .apply(
          FakeRequest(
            POST,
            "/pass-bakery/product"
          ).withBody("""{"name": "crepe", "quantity": 5, "price": 4.99}""")
            .withHeaders(("Content-Type:", "application/json"))
        )
      status(result) mustBe 400
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
  "Delete product" should {
    "return successful when deleting a product from the database" in {
      val controller = inject[APIController]
      val uuid: UUID = UUID.randomUUID()
      val product =
        ProductFromJson(Some("crepe"), Some(5), Some(4.99), Some(uuid))
      val fakePostRequest = FakeRequest(
        POST,
        controllers.routes.APIController.postProduct().url
      ).withBody(product)
        .withHeaders(("Content-Type" -> "application/json"))

      val resultPost = controller
        .postProduct()
        .apply(
          fakePostRequest
        )

      info(status(resultPost).toString)

      val fakeDeleteRequest = FakeRequest(
        DELETE,
        controllers.routes.APIController.deleteByID(uuid).url
      )

      val result = controller
        .deleteByID(uuid)
        .apply(
          fakeDeleteRequest
        )

      status(result) mustBe (200)
    }
  }

  database.shutdown()
}
