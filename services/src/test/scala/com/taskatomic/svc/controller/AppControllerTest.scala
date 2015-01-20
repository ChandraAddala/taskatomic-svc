package com.taskatomic.svc.controller

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.taskatomic.svc.dto.UserDTO
import com.taskatomic.svc.model.DBTables._
import com.taskatomic.svc.model.{Task, Project, User, InitDBTrait}
import org.junit.runner.RunWith
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest.ScalatraSuite

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database

import scala.util.Random

import org.json4s._
import org.json4s.jackson.JsonMethods._

@RunWith(classOf[JUnitRunner])
class AppControllerTest extends ScalatraSuite with FunSuiteLike with InitDBTrait {

  val db = Database.forDataSource(new ComboPooledDataSource)
  addServlet(new AppController(db), "/*")

  protected implicit val jsonFormats: Formats = DefaultFormats
  
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

  test("should get 201: POST to create new user /users") {
    val handle: String = Random.nextInt().toString
    val requestBody: String = 
      """
        |{"handle": "%s",
        |"firstName":"x",
        |"lastName":"y"}
      """.stripMargin.format(handle)

    post ("/users", requestBody, Map("Content-Type" -> "application/json")) {
      status should equal (201)
      val user: User = parse(body).extract[User] 
      assert(user.handle === handle, "handle does not match with the handle of user created")
    }
    
  }

  test("should get 200: POST to create existing user /users") {
    val handle: String = Random.nextInt().toString
    createUser(db, handle, "Jason", "Borne")
    
    val requestBody: String =
      """
        |{"handle": "%s",
        |"firstName":"x",
        |"lastName":"y"}
      """.stripMargin.format(handle)

    post ("/users", requestBody, Map("Content-Type" -> "application/json")) {
      status should equal (200)
      val user: User = parse(body).extract[User]
      assert(user.handle === handle, "handle does not match with the handle of user created")
      assert(user.firstName === Some("Jason"), "handle does not match with the handle of user created")
    }

  }
  
  test("should get 200: PUT to update user /users/:handle") {
    val handle: String = Random.nextInt().toString
    createUser(db, handle, "Jason", "Borne")
    val userRowId: Option[Int] = db.withSession { implicit session =>
      UserTable.filter(_.handle === handle).map(_.id).run.head
    }

    val requestBody: String =
      """
        |{"handle": "%s",
        |"firstName":"x",
        |"lastName":"y"}
      """.stripMargin.format(handle)

    put ("/users/" + handle, requestBody, Map("Content-Type" -> "application/json")) {
      status should equal (200)
      val user: User = parse(body).extract[User]
      assert(user.id === userRowId, "handle does not match with the handle of user updated")
      assert(user.firstName === Some("x"), "first name of the user updated does not match with first name in request")
    }

  }
  
  test("should get 201: POST to create project /users/:handle/projects") {
    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString

    createUser(db, handle, "Jason", "Borne")
    val userRowId: Option[Int] = db.withSession { implicit session =>
      UserTable.filter(_.handle === handle).map(_.id).run.head
    }
    
    val requestBody = 
      """
        |{"projectName":"%s",
        |"projectOwner": %s}
      """.stripMargin.format(projectName, userRowId.get)
    
    post("/users/" + handle + "/projects", requestBody, Map("Content-Type" -> "application/json")) {
      status should equal (201)
      val project: Project = parse(body).extract[Project]
      assert(project.projectName === projectName, "project name does not match with the name of the project created")
    }
  }

  test("should get 200: PUT to update project /users/:handle/projects/:id") {
    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString

    createUser(db, handle, "Jason", "Borne")
    val projectId = createProject(db, handle, projectName)
    val userRowId: Option[Int] = db.withSession { implicit session =>
      UserTable.filter(_.handle === handle).map(_.id).run.head
    }

    val requestBody =
      """
        |{"projectName":"x",
        |"projectOwner": %s}
      """.stripMargin.format(userRowId.get)

    put("/users/" + handle + "/projects/" + projectId, requestBody, Map("Content-Type" -> "application/json")) {
      status should equal (200)
      val project: Project = parse(body).extract[Project]
      assert(project.id === Some(projectId), "project id does not match")
      assert(project.projectName === "x", "project name does not match with the name of the project created")
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
      val userDTO: UserDTO = parse(body).extract[UserDTO]
      assert(userDTO.projects.size == 1, "Number of project associated with user does not match")
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
      
      val projects: List[Project] = parse(body).extract[List[Project]]
      assert(projects.size == 1, "Number of project associated with user does not match")
    }

    get ("/users/" + handle + "/projects") {
      status should equal (200)
      response.mediaType should equal (Some("application/json"))

      val projects: List[Project] = parse(body).extract[List[Project]]
      assert(projects.size == 1, "Number of project associated with user does not match")
    }
  }
  
  test("should get 201: POST to create task /users/:user_handle/projects/:projectId/tasks") {
    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    val taskName: String = Random.nextInt().toString

    createUser(db, handle, "Jason", "Borne")
    val userRowId: Option[Int] = db.withSession { implicit session =>
      UserTable.filter(_.handle === handle).map(_.id).run.head
    }
    val projectId = createProject(db, handle, projectName)
    
    val requestBody = 
      """
        |{"taskName":"%s",
        |"projectId": %s,
        |"createdBy": %s,
        |"assignedTo": %s,
        |"percentageComplete":%s}
      """.stripMargin.format(taskName, projectId, userRowId.get, userRowId.get, "12.5")
    post("/users/" + handle + "/projects/" + projectId + "/tasks", requestBody, Map("Content-Type" -> "application/json")) {
      status should equal (201)
      val task: Task = parse(body).extract[Task]
      assert(task.taskName === taskName, "project name does not match with the name of the project created")
    }
  }

  test("should get 500: POST to create task /users/:user_handle/projects/:projectId/tasks") {
    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    val taskName: String = Random.nextInt().toString

    createUser(db, handle, "Jason", "Borne")
    val userRowId: Option[Int] = db.withSession { implicit session =>
      UserTable.filter(_.handle === handle).map(_.id).run.head
    }
    val projectId = createProject(db, handle, projectName)

    val requestBody =
      """
        |{"taskName":"%s",
        |"projectId": %s,
        |"createdBy": %s,
        |"assignedTo": %s,
        |"percentageComplete":%s}
      """.stripMargin.format(taskName, projectId, userRowId.get, userRowId.get, "12.5")

    //request with invalid project id
    post("/users/" + handle + "/projects/" + Random.nextInt() + "/tasks", requestBody, Map("Content-Type" -> "application/json")) {
      status should equal (400)
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

  test("should get 200: PUT to update task /users/:handle/projects/:projectId/tasks/:id") {
    val handle: String = Random.nextInt().toString
    val projectName: String = Random.nextInt().toString
    val taskName: String = Random.nextInt().toString

    createUser(db, handle, "Jason", "Borne")
    val userRowId: Option[Int] = db.withSession { implicit session =>
      UserTable.filter(_.handle === handle).map(_.id).run.head
    }
    val projectId = createProject(db, handle, projectName)
    val taskId = createTask(db, handle, projectId, taskName)

    val requestBody =
      """
        |{"taskName":"x",
        |"projectId": %s,
        |"createdBy": %s,
        |"assignedTo": %s,
        |"percentageComplete":%s}
      """.stripMargin.format(projectId, userRowId.get, userRowId.get, "12.5")
    put("/users/" + handle + "/projects/" + projectId + "/tasks/" + taskId, requestBody, Map("Content-Type" -> "application/json")) {
      status should equal (200)
      val task: Task = parse(body).extract[Task]
      assert(task.id === Some(taskId), "task id does not match")
      assert(task.taskName === "x", "project name does not match with the name of the project created")
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
