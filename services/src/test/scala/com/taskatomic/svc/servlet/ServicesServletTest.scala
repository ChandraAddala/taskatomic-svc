package com.taskatomic.svc.servlet

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.taskatomic.svc.model.{Project, User, InitDBTrait}
import org.junit.runner.RunWith
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest.ScalatraSuite

import scala.slick.jdbc.JdbcBackend._
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class ServicesServletTest extends ScalatraSuite with FunSuiteLike with InitDBTrait {

  val db = Database.forDataSource(new ComboPooledDataSource)
  addServlet(new ServicesServlet(db), "/*")

  override def beforeAll {
    super.beforeAll
    clearData(db)
  }

  test("should get 404: GET /users/:handle") {
    val handle: String = Random.nextInt().toString
    
    get ("/users/" + handle) {
      status should equal (404)
      response.mediaType should equal (Some("application/json"))
    }
  }
  
  test("should get 200: GET /users/:handle") {

    val handle: String = Random.nextInt().toString
    createUser(db, handle, "Jason", "Borne")
    
    get ("/users/" + handle) {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))
    }
  }

  test("should get 404: GET /users/:handle/projects/:projectName") {

    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString

    get ("/users/" + handle + "/projects/" + projectName) {
      status should equal (404)
      response.mediaType should equal (Some("application/json"))
    }
  }
  
  test("should get 200: GET /users/:handle/projects/:projectName") {

    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    
    createUser(db, handle, "Jason", "Borne")
    createProject(db, handle, projectName)
    
    get ("/users/" + handle + "/projects/" + projectName) {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))
    }
  }

  test("should get 404: GET /users/:handle/projects/:projectName/tasks/:taskName") {

    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    val taskName: String = Random.nextInt().toString

    get ("/users/" + handle + "/projects/" + projectName + "/tasks/" + taskName) {
      status should equal (404)
      response.mediaType should equal (Some("application/json"))
    }
  }
  
  test("should get 200: GET /users/:handle/projects/:projectName/tasks/:taskName") {

    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    val taskName: String = Random.nextInt().toString

    createUser(db, handle, "Jason", "Borne")
    createProject(db, handle, projectName)
    createTask(db, handle, projectName, taskName)

    get ("/users/" + handle + "/projects/" + projectName + "/tasks/" + taskName) {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))
    }
  }


  override def afterAll() {
    super.afterAll()
    clearData(db)
  }
}
