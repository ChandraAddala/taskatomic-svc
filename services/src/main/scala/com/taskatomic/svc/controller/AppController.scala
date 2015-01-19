package com.taskatomic.svc.controller

import com.taskatomic.svc.dao.AppDao
import com.taskatomic.svc.dto.UserDTO
import com.taskatomic.svc.model.{Task, Project, User}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport
import org.slf4j.LoggerFactory

import scala.slick.jdbc.JdbcBackend.Database

import org.json4s._
import org.json4s.jackson.JsonMethods._

class AppController(db: Database) extends ScalatraServlet with ScalateSupport with JacksonJsonSupport with MethodOverride {

  protected implicit val jsonFormats: Formats = DefaultFormats
  implicit val appDao: AppDao = new AppDao(db)
  
  val logger = LoggerFactory.getLogger(getClass)

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }
  
  get("/") {
    "Taskatomic services"
  }
  
  get("/users/:user_handle") {
    val userHandle = params("user_handle")

    appDao.getUser(db, userHandle) match {
      case Some(user: User) => Ok(user)
      case _ => NotFound("User not found")
    }
  }

  post("/users") {
    val user: User = parse(request.body).extract[User]
    
    Created(appDao.saveUser(db, user))
  }
  
  put("/users/:user_handle") {
    val userHandle = params("user_handle")
    val user: User = parse(request.body).extract[User]
    
    Ok(appDao.updateUser(db, userHandle, user))
  }
  
  get("/users/:user_handle/all") {
    val userHandle = params("user_handle")

    appDao.getUser(db, userHandle) match {
      case Some(user: User) =>  Ok(UserDTO(user, appDao.getProjects(db, userHandle)))
      case _ => NotFound("User not found")
    }
  }

  get("/users/:user_handle/projects") {
    val userHandle = params("user_handle")

    Ok(appDao.getProjects(db, userHandle))
  }
  
  post("/users/:user_handle/projects") {
    val userHandle = params("user_handle")
    val project: Project = parse(request.body).extract[Project]
    
    try {
      Created(appDao.saveProject(db, project, userHandle))
    } catch {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  put("/users/:user_handle/projects/:id") {
    val userHandle = params("user_handle")
    val projectId = params("id").toInt
    val project: Project = parse(request.body).extract[Project]

    Ok(appDao.updateProject(db, projectId, project, userHandle))
  }
  
  get("/users/:user_handle/projects/:id") {
    val userHandle = params("user_handle")
    val projectId = params("id").toInt
    
    appDao.getProject(db, userHandle, projectId) match {
      case Some(project: Project) => Ok(project)
      case _ => NotFound("Project not found")
    }
  }

  get("/users/:user_handle/projects/:projectId/tasks") {
    val userHandle = params("user_handle")
    val projectId = params("projectId").toInt

    Ok(appDao.getTasks(db, userHandle, projectId))
  }
  
  post("/users/:user_handle/projects/:projectId/tasks") {
    val userHandle = params("user_handle")
    val projectId = params("projectId").toInt
    val task: Task = parse(request.body).extract[Task]
    
    try {
      Created(appDao.saveTask(db, userHandle, projectId, task))
    } catch {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  put("/users/:user_handle/projects/:projectId/tasks/:id") {
    val userHandle = params("user_handle")
    val projectId = params("projectId").toInt
    val taskId = params("id").toInt
    val task: Task = parse(request.body).extract[Task]

    appDao.updateTask(db, userHandle, projectId, taskId, task)
  }
  
  get("/users/:user_handle/projects/:projectId/tasks/:id") {
    val userHandle = params("user_handle")
    val projectId = params("projectId").toInt
    val taskId = params("id").toInt

    appDao.getTask(db, userHandle, projectId, taskId) match {
      case Some(task: Task) => Ok(task)
      case _ => NotFound("Task not found")
    }
  }

  error {
    case e => {
      logger.error("Request failed with exception", e);
      InternalServerError("Request failed with exception:" + e + " message:" + e.getMessage)
    }
  }
}
