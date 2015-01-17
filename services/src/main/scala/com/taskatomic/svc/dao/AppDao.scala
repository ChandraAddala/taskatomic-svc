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
        ((project, task), user) <- ProjectTable leftJoin TaskTable on (_.id === _.projectId) innerJoin UserTable on ((tuple, u) => (tuple._1.projectOwner === u.id) || (tuple._2.assignedTo === u.id))
        if user.handle === handle
      } yield (project)

      getProjectQuery.list.distinct
    }
  }
  
  def getProject(db:Database, handle:String, id: Int): Option[Project] = {
    db.withSession { implicit session =>
      
      val getProjectQuery = for {
        ((project, task), user) <- ProjectTable leftJoin TaskTable on (_.id === _.projectId) innerJoin UserTable on ((tuple, u) => (tuple._1.projectOwner === u.id) || (tuple._2.assignedTo === u.id))
        if project.id === id && user.handle === handle
      } yield (project)

      getProjectQuery.list.distinct match {
        case List(project: Project) => Some(project)
        case _ => None
      }    
    }
  }
  
  def getTasks(db:Database, projectId: Int):  List[Task] = {
    db.withSession { implicit session =>
      val getTaskQuery = for {
        (task, project) <- TaskTable innerJoin ProjectTable on (_.projectId === _.id)
        if project.id === projectId
      } yield (task)

      getTaskQuery.list.distinct
    }
  }

  def getTask(db:Database, projectId: Int, id: Int):  Option[Task] = {
    db.withSession { implicit session =>
      val getTaskQuery = for {
        (task, project) <- TaskTable innerJoin ProjectTable on (_.projectId === _.id)
        if task.id === id && project.id === projectId
      } yield (task)

      getTaskQuery.list.distinct match {
        case List(task: Task) => Some(task)
        case _ => None
      }
    }
  }
  
  def getTasks(db:Database, handle:String, projectId: Int): List[Task] = {
    //if user has this project associated(either as a owner of assignee of one of the tasks)
    getProject(db, handle, projectId) match {
      case Some(project: Project) => getTasks(db, projectId)
      case _ => List[Task]()
    }
  }
  
  def getTask(db:Database, handle:String, projectId: Int, id: Int): Option[Task] = {
    //if user has this project associated(either as a owner of assignee of one of the tasks)
    getProject(db, handle, projectId) match {
      case Some(project: Project) => getTask(db, projectId, id)
      case _ => None
    }
  }
}
