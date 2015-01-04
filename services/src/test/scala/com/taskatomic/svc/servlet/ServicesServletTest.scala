package com.taskatomic.svc.servlet

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.taskatomic.svc.model.InitDBTrait
import org.junit.runner.RunWith
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest.ScalatraSuite

import scala.slick.jdbc.JdbcBackend._

@RunWith(classOf[JUnitRunner])
class ServicesServletTest extends ScalatraSuite with FunSuiteLike with InitDBTrait {

  val db = Database.forDataSource(new ComboPooledDataSource)
  addServlet(new ServicesServlet(db), "/*")

  override def beforeAll {
    super.beforeAll
    clearData(db)
  }
  
  test("should get 200: GET /users/sample_user") {
    get ("/users/sample_user") {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))
    }
  }
  
}
