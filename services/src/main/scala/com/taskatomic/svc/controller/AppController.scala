package com.taskatomic.svc.controller

import com.taskatomic.svc.dao.AppDao
import com.taskatomic.svc.dto.UserDTO
import com.taskatomic.svc.model.{Task, Project, User}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{InternalServerError, NotFound, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport
import org.slf4j.LoggerFactory

import scala.slick.jdbc.JdbcBackend.Database

class AppController(db: Database) extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats
  implicit val servicesDao: AppDao = new AppDao(db)
  
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

    servicesDao.getUser(db, userHandle) match {
      case Some(user: User) => user
      case None => NotFound("User not found")
    }
  }

  get("/users/:user_handle/all") {
    val userHandle = params("user_handle")

    servicesDao.getUser(db, userHandle) match {
      case Some(user: User) =>  UserDTO(user, servicesDao.getProjects(db, userHandle))
      case None => NotFound("User not found")
    }
  }
  
  get("/users/:user_handle/projects/:id") {
    val userHandle = params("user_handle")
    val projectId = params("id").toInt
    
    servicesDao.getProject(db, userHandle, projectId) match {
      case Some(project: Project) => project
      case None => NotFound("Project not found")
    }
  }

  get("/users/:user_handle/projects/:projectId/tasks/:id") {
    val userHandle = params("user_handle")
    val projectId = params("projectId").toInt
    val taskId = params("id").toInt

    servicesDao.getTask(db, userHandle, projectId, taskId) match {
      case Some(task: Task) => task
      case None => NotFound("Task not found")
    }
  }

  error {
    case e => {
      logger.error("Request failed with exception", e);
      InternalServerError("Request failed with exception:" + e + " message:" + e.getMessage)
    }
  }
}
