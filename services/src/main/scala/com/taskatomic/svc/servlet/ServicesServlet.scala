package com.taskatomic.svc.servlet

import com.taskatomic.svc.dao.ServicesDao
import com.taskatomic.svc.model.{Task, Project, User}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{NotFound, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

import scala.slick.jdbc.JdbcBackend.Database

class ServicesServlet(db: Database) extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats
  implicit val servicesDao: ServicesDao = new ServicesDao(db)

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
  
  get("/users/:user_handle/projects/:project_name") {
    val userHandle = params("user_handle")
    val projectName = params("project_name")
    
    servicesDao.getProject(db, userHandle, projectName) match {
      case Some(project: Project) => project
      case None => NotFound("Project not found")
    }
  }

  get("/users/:user_handle/projects/:project_name/tasks/:task_name") {
    val userHandle = params("user_handle")
    val projectName = params("project_name")
    val taskName = params("task_name")

    servicesDao.getTask(db, userHandle, projectName, taskName) match {
      case Some(task: Task) => task
      case None => NotFound("Task not found")
    }
  }
}
