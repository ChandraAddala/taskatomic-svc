package com.taskatomic.svc.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.taskatomic.svc.model.DBTables._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.slf4j.LoggerFactory

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

class DBTablesIT extends FunSuite with BeforeAndAfter with BeforeAndAfterAll with InitDBTrait{

  val logger = LoggerFactory.getLogger(getClass)
  val pooledDataSource = new ComboPooledDataSource
  val db : Database = Database.forDataSource(pooledDataSource)

  after {
    clearData(db)
  }
  
  test ("Verify insertion of data into table User") {

    val testUserId = 1
    
    db withDynSession {
      User.map(user => (user.userId, user.firstName, user.lastName))
          .insert(testUserId, Some("Jason"), Some("Borne"))
    }
    
    val isUserPresent = db withDynSession {
      User.filter(_.userId === testUserId).run.nonEmpty
    }

    assert(isUserPresent, "row not inserted in table User")
  }
  
  
  test ("Verify insertion of data into table Project") {

    val testProjectName = "diwali_celeb"
    val testUserId = 1
    
    db withDynSession {
      User.map(user => (user.userId, user.firstName, user.lastName))
        .insert( testUserId, Some("Jason"), Some("Borne"))
      
      val userRowId: Option[Int] = User.filter(_.userId === testUserId).map(_.id).run.head
      
      Project.map(project => (project.projectName, project.projectOwner))
          .insert(testProjectName, userRowId.get)
    }

    val isProjectPresent = db withDynSession {
      Project.filter(_.projectName === testProjectName).run.nonEmpty
    }

    assert(isProjectPresent, "row not inserted in table Project")
  }
  
  test ("Verify insertion of data into table Task") {

    val testProjectName = "diwali_celeb"
    val testUserId = 1
    val testTaskName1 = "bring_sweets"
    
    db withDynSession {
      User.map(user => (user.userId, user.firstName, user.lastName))
        .insert( testUserId, Some("Jason"), Some("Borne"))

      val userRowId: Option[Int] = User.filter(_.userId === testUserId).map(_.id).run.head

      Project.map(project => (project.projectName, project.projectOwner))
        .insert(testProjectName, userRowId.get)
      
      val projectRowId: Option[Int] = Project.filter(_.projectName === testProjectName).map(_.id).run.head
      
      Task.map(task => (task.taskName, task.projectId, task.createdBy, task.assignedTo, task.percentageComplete))
        .insert(testTaskName1, projectRowId.get, userRowId.get, userRowId.get, Some(0.0))
    }

    val isTaskPresent = db withDynSession {
      Task.filter(_.taskName === testTaskName1).run.nonEmpty
    }

    assert(isTaskPresent, "row not inserted in table task")
  }
  
  test ("Verify assigning multiple tasks to a project") {

    val testProjectName = "diwali_celeb"
    val testUserId = 1
    val testTaskName1 = "bring_sweets"
    val testTaskName2 = "bring_crackers"

    var projectRowId: Option[Int] = None
    
    db withDynSession {
      User.map(user => (user.userId, user.firstName, user.lastName))
        .insert( testUserId, Some("Jason"), Some("Borne"))

      val userRowId: Option[Int] = User.filter(_.userId === testUserId).map(_.id).run.head

      Project.map(project => (project.projectName, project.projectOwner))
        .insert(testProjectName, userRowId.get)

      projectRowId = Project.filter(_.projectName === testProjectName).map(_.id).run.head

      Task.map(task => (task.taskName, task.projectId, task.createdBy, task.assignedTo, task.percentageComplete))
        .insert(testTaskName1, projectRowId.get, userRowId.get, userRowId.get, Some(0.0))
      
      Task.map(task => (task.taskName, task.projectId, task.createdBy, task.assignedTo, task.percentageComplete))
        .insert(testTaskName2, projectRowId.get, userRowId.get, userRowId.get, Some(0.0))
    }
    
    val numberOfTasks = db withDynSession {
      Task.filter(_.projectId === projectRowId).length.run
    }
    
    assert(numberOfTasks == 2, "Number of tasks assigned to project does not match")
  }
  
  override def afterAll {
    pooledDataSource.close
  }
}
