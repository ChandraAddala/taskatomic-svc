package com.taskatomic.svc.dao

import com.taskatomic.svc.dto.UserDTO
import com.taskatomic.svc.model.DBTables._
import com.taskatomic.svc.model.{Task, Project, User}
import org.scalatra._

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
  
  def saveUser(db: Database, user: User): User = {
    db.withSession {implicit session =>
      UserTable.map(user => (user.handle, user.firstName, user.lastName))
        .insert(user.handle, user.firstName, user.lastName)
      
      getUser(db, user.handle).get
    }
  }

  def updateUser(db: Database, handle:String, user: User): User = {
    db.withSession {implicit session =>
      UserTable.filter(_.handle === handle)
        .map(user => (user.handle, user.firstName, user.lastName))
        .update(user.handle, user.firstName, user.lastName)

      getUser(db, handle).get
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
  
  def saveProject(db: Database, project: Project, handle: String): Project = {
    db.withSession { implicit session =>

      getUser(db, handle) match {
        case Some(user: User) =>  {
          ProjectTable.map(project => (project.projectName, project.projectOwner))
            .insert(project.projectName, user.id.get)

          ProjectTable.filter(_.projectName === project.projectName).run.head
        }
        case _ => throw new IllegalArgumentException("User not found:" + handle)
      }
    }
  }

  def updateProject(db: Database, projectId: Int, project: Project, handle: String): Project = {
    db.withSession { implicit session =>

      getUser(db, handle) match {
        case Some(user: User) =>  {
          ProjectTable.filter(p => p.id === projectId && p.projectOwner === user.id.get)
            .map(project => (project.projectName))
            .update(project.projectName)

          ProjectTable.filter(_.id === projectId).run.head
        }
        case _ => throw new IllegalArgumentException("User not found:" + handle)
      }
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

  def saveTask(db:Database, handle:String, projectId: Int, task: Task): Task = {
    db.withSession { implicit session =>

      getUser(db, handle) match {
        case Some(user: User) =>  {
          TaskTable.map(task => (task.taskName, task.projectId, task.createdBy, task.assignedTo, task.percentageComplete))
            .insert(task.taskName, projectId, user.id.get, user.id.get, Some(0.0))

          TaskTable.filter(t => t.projectId === projectId && t.taskName === task.taskName).run.head
        }
        case None => throw new IllegalArgumentException("User not found:" + handle)
      }
    }
  }

  def updateTask(db: Database, handle: String, projectId: Int, id: Int, task: Task): Task = {
    db.withSession { implicit session =>

      getUser(db, handle) match {
        case Some(user: User) =>  {
          //TODO: Verify user is the owner on the project.
          TaskTable.filter(t => t.projectId === projectId && t.id === id)
            .map(task => (task.taskName, task.assignedTo, task.percentageComplete))
            .update(task.taskName, task.assignedTo, Some(0.0))

          TaskTable.filter(t => t.projectId === projectId && t.id === id).run.head
        }
        case None => throw new IllegalArgumentException("User not found:" + handle)
      }
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
