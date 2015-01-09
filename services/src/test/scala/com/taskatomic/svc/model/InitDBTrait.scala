package com.taskatomic.svc.model


import com.taskatomic.svc.model.DBTables._
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

trait InitDBTrait {
  
  def createUser(db:Database, handle:String, firstName: String, lastName: String) {
    db withDynSession {
      UserTable.map(user => (user.handle, user.firstName, user.lastName))
        .insert(handle, Some(firstName), Some(lastName))
    }
  }
  
  def createProject(db:Database, userHandle: String, projectName: String) {
    db withDynSession {
      val userRowId: Option[Int] = UserTable.filter(_.handle === userHandle).map(_.id).run.head

      ProjectTable.map(project => (project.projectName, project.projectOwner))
        .insert(projectName, userRowId.get)
    }
  }

  def createTask(db:Database, userHandle: String, projectName: String, taskName:String) = {
    db withDynSession {
      val userRowId: Option[Int] = UserTable.filter(_.handle === userHandle).map(_.id).run.head
      val projectRowId: Option[Int] = ProjectTable.filter(_.projectName === projectName).map(_.id).run.head

      TaskTable.map(task => (task.taskName, task.projectId, task.createdBy, task.assignedTo, task.percentageComplete))
        .insert(taskName, projectRowId.get, userRowId.get, userRowId.get, Some(0.0))
    }
  }
  
  def clearData(db: Database) {
    db withDynSession {
      TaskTable.delete
      ProjectTable.delete
      UserTable.delete
    }
  }
}
