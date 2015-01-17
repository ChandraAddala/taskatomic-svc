package com.taskatomic.svc.controller

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.taskatomic.svc.model.InitDBTrait
import org.junit.runner.RunWith
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest.ScalatraSuite

import scala.slick.jdbc.JdbcBackend._
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class AppControllerTest extends ScalatraSuite with FunSuiteLike with InitDBTrait {

  val db = Database.forDataSource(new ComboPooledDataSource)
  addServlet(new AppController(db), "/*")

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

  test("should get 404: GET /users/:handle/projects/:projectId") {

    val handle: String = Random.nextInt().toString
    val projectId: Int = Random.nextInt()

    get ("/users/" + handle + "/projects/" + projectId) {
      status should equal (404)
      response.mediaType should equal (Some("application/json"))
    }
  }
  
  test("should get 200: GET /users/:handle/projects/:id") {

    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    
    createUser(db, handle, "Jason", "Borne")
    val projectId = createProject(db, handle, projectName)
    
    get ("/users/" + handle + "/projects/" + projectId) {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))
    }
  }

  test("should get 200: GET /users/:handle/all") {

    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString

    createUser(db, handle, "Jason", "Borne")
    val projectId = createProject(db, handle, projectName)

    get ("/users/" + handle + "/all") {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))
      //TODO: Verify we got UserDTO and verify the number of projects associated to user.
    }
  }

  test("should get 200: GET /users/:handle/projects") {

    val handle: String = Random.nextInt().toString
    val handle1: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    val taskName: String = Random.nextInt().toString
    val taskName1: String = Random.nextInt().toString
    
    createUser(db, handle, "Jason", "Borne")
    createUser(db, handle1, "Jason1", "Borne1")
    val projectId = createProject(db, handle, projectName)

    val taskId = createTask(db, handle, projectId, taskName)
    //create task and assign it to a person(handle1) other than owner(handle)
    val taskId1 = createTask(db, handle1, projectId, taskName1)
    
    // getting projects of a user who has only one assigned 
    // task but is not an owner of any project
    get ("/users/" + handle1 + "/projects") {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))
      
      //TODO: Verify size of the project array is 1
    }

    get ("/users/" + handle + "/projects") {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))

      //TODO: Verify size of the project array is 1
    }
  }
  
  test("should get 404: GET /users/:handle/projects/:projectName/tasks/:id") {

    val handle: String = Random.nextInt().toString
    val projectId: Int = Random.nextInt()
    val taskId: Int = Random.nextInt()

    get ("/users/" + handle + "/projects/" + projectId + "/tasks/" + taskId) {
      status should equal (404)
      response.mediaType should equal (Some("application/json"))
    }
  }
  
  test("should get 200: GET /users/:handle/projects/:projectId/tasks/:id") {

    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    val taskName: String = Random.nextInt().toString

    createUser(db, handle, "Jason", "Borne")
    val projectId = createProject(db, handle, projectName)
    val taskId = createTask(db, handle, projectId, taskName)

    get ("/users/" + handle + "/projects/" + projectId + "/tasks/" + taskId) {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))
    }
  }


  override def afterAll() {
    super.afterAll()
    clearData(db)
  }
}
