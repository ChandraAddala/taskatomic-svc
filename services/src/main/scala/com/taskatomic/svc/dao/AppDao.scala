package com.taskatomic.svc.dao

import com.taskatomic.svc.model.DBTables._
import com.taskatomic.svc.model.{Task, Project, User}

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database

class AppDao(db: Database) {

  def getUser(db: Database, handle: String): Option[User] = {
    db.withSession { implicit session =>
      UserTable.filter(_.handle === handle).list match {
        case List(user: User) => Some(user)
        case _ => None
      }
    }
  }
  
  def getProjects(db:Database, handle:String) : List[Project] = {
    db.withSession { implicit session =>
      val getProjectQuery = for {
        (project, user) <- ProjectTable innerJoin UserTable on (_.projectOwner === _.id)
        if user.handle === handle
      } yield (project)

      getProjectQuery.list
    }
  }
  
  def getProject(db:Database, handle:String, id: Int): Option[Project] = {
    db.withSession { implicit session =>
      
      val getProjectQuery = for {
        (project, user) <- ProjectTable innerJoin UserTable on (_.projectOwner === _.id)
        if project.id === id && user.handle === handle
      } yield (project)

      getProjectQuery.list match {
        case List(project: Project) => Some(project)
        case _ => None
      }    
    }
  }

  def getTask(db:Database, handle:String, projectId: Int, id: Int): Option[Task] = {
    db.withSession { implicit session =>
      val getTaskQuery = for {
        ((task, project), user) <- TaskTable innerJoin ProjectTable on (_.projectId === _.id) innerJoin UserTable on (_._2.projectOwner === _.id)
        if task.id === id && project.id === projectId && user.handle === handle
      } yield (task)

      getTaskQuery.list match {
        case List(task: Task) => Some(task)
        case _ => None
      }
    }
  }
}
