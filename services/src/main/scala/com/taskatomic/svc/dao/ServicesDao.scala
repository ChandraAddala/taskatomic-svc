package com.taskatomic.svc.dao

import com.taskatomic.svc.model.DBTables._
import com.taskatomic.svc.model.{Task, Project, User}

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

class ServicesDao(db: Database) {

  def getUser(db: Database, handle: String): Option[User] = {
    db withDynSession {
      UserTable.filter(_.handle === handle).list match {
        case List(user: User) => Some(user)
        case _ => None
      }
    }
  }
  
  def getProject(db:Database, handle:String, projectName: String): Option[Project] = {
    db withDynSession {
      
      val getProjectQuery = for {
        (project, user) <- ProjectTable innerJoin UserTable on (_.projectOwner === _.id)
        if project.projectName === projectName && user.handle === handle
      } yield (project)

      getProjectQuery.list match {
        case List(project: Project) => Some(project)
        case _ => None
      }    
    }
  }
  
  def getTask(db:Database, handle:String, projectName: String, taskName: String): Option[Task] = {
    db withDynSession {
      val getTaskQuery = for {
        ((task, project), user) <- TaskTable innerJoin ProjectTable on (_.projectId === _.id) innerJoin UserTable on (_._2.projectOwner === _.id)
        if (task.taskName === taskName && project.projectName === projectName && user.handle === handle)
      } yield (task)

      getTaskQuery.list match {
        case List(task: Task) => Some(task)
        case _ => None
      }
    }
  }
}
