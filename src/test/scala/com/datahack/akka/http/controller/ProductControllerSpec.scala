package com.datahack.akka.http.controller

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestActorRef
import com.datahack.akka.http.controller.actors.ProductControllerActor
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.{JsonSupport, Product}
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import spray.json._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ProductControllerSpec
  extends WordSpec
    with Matchers
    with Generators
    with BeforeAndAfterAll
    with Directives
    with ScalatestRouteTest
    with JsonSupport {

  var schemaName = ""

  var products: Seq[Product] = (1 to 5).map(i => genProduct.sample.get.copy(id = Some(i)))

  val productDao = new ProductDao()
  val productService = new ProductService(productDao)
  val productControllerActor = TestActorRef[ProductControllerActor](new ProductControllerActor(productService))
  val productController = new ProductController(productControllerActor)

  override protected def beforeAll(): Unit = {
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    Await.result(Future.sequence(SqlTestUtils.insertList(products.toList, schemaName)), 5 seconds)
  }


  "Product Controller" should {

    "get all products offered in the application" in {
      Get("/products") ~> productController.routes ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Product]]
        response.length shouldBe products.length
      }
    }

    "get specific product offered by it id" in {
      Get(s"/products/${products.head.id.get}") ~> productController.routes ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Product]
        response shouldBe products.head
      }
    }

    "get not found status code when searching a product that is not offered" in {

    }

    "add a new prdouct" in {

    }

    "update the product data" in {

    }

    "get Not Found status code when trying to update a product that is not offered" in {

    }

    "delete an offered product" in {

    }

    "get Not Found status code when trying to delete a product not offered" in {

    }
  }
}
