package com.taskatomic.svc.model

import java.sql.Timestamp

import org.joda.time.DateTime

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.{GetResult => GR}
import scala.slick.model.ForeignKeyAction

object DBTables {
  
  //org.joda.time.DateTime -> DateTime mapper
  implicit def dateTime2TimeStamp  = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime)
  )

  /** GetResult implicit for fetching ProjectRow objects using plain SQL queries */
  implicit def GetResultProjectRow(implicit e0: GR[Option[Int]], e1: GR[String], e2: GR[Int], e3: GR[Option[DateTime]]): GR[Project] = GR{
    prs => import prs._
      Project.tupled((<<?[Int], <<[String], <<[Int], <<?[DateTime], <<?[DateTime]))
  }
  /** Table description of table project. Objects of this class serve as prototypes for rows in queries. */
  class ProjectTableDescription(_tableTag: Tag) extends Table[Project](_tableTag, "project") {
    def * = (id, projectName, projectOwner, created, updated) <> (Project.tupled, Project.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id, projectName.?, projectOwner.?, created, updated).shaped.<>({r=>import r._; _2.map(_=> Project.tupled((_1, _2.get, _3.get, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INTEGER) */
    val id: Column[Option[Int]] = column[Option[Int]]("id")
    /** Database column project_name DBType(VARCHAR), Length(20,true) */
    val projectName: Column[String] = column[String]("project_name", O.Length(20,varying=true))
    /** Database column project_owner DBType(INTEGER) */
    val projectOwner: Column[Int] = column[Int]("project_owner")
    /** Database column created DBType(TIMESTAMP) */
    val created: Column[Option[DateTime]] = column[Option[DateTime]]("created")
    /** Database column updated DBType(TIMESTAMP) */
    val updated: Column[Option[DateTime]] = column[Option[DateTime]]("updated")

    /** Primary key of Project (database name CONSTRAINT_E) */
    val pk = primaryKey("CONSTRAINT_E", (projectName, projectOwner))

    /** Foreign key referencing User (database name CONSTRAINT_ED) */
    lazy val userFk = foreignKey("CONSTRAINT_ED", projectOwner, UserTable)(r => r.id.get, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)

    /** Uniqueness Index over (id) (database name CONSTRAINT_INDEX_ED) */
    val index1 = index("CONSTRAINT_INDEX_ED", id, unique=true)
  }
  /** Collection-like TableQuery object for table Project */
  lazy val ProjectTable = new TableQuery(tag => new ProjectTableDescription(tag))

  /** GetResult implicit for fetching TaskRow objects using plain SQL queries */
  implicit def GetResultTaskRow(implicit e0: GR[Option[Int]], e1: GR[String], e2: GR[Int], e3: GR[Option[DateTime]], e4: GR[Option[scala.math.BigDecimal]]): GR[Task] = GR{
    prs => import prs._
      Task.tupled((<<?[Int], <<[String], <<[Int], <<[Int], <<[Int], <<?[DateTime], <<?[DateTime], <<?[scala.math.BigDecimal]))
  }
  /** Table description of table task. Objects of this class serve as prototypes for rows in queries. */
  class TaskTableDescription(_tableTag: Tag) extends Table[Task](_tableTag, "task") {
    def * = (id, taskName, projectId, createdBy, assignedTo, created, updated, percentageComplete) <> (Task.tupled, Task.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id, taskName.?, projectId.?, createdBy.?, assignedTo.?, created, updated, percentageComplete).shaped.<>({r=>import r._; _2.map(_=> Task.tupled((_1, _2.get, _3.get, _4.get, _5.get, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INTEGER) */
    val id: Column[Option[Int]] = column[Option[Int]]("id")
    /** Database column task_name DBType(VARCHAR), Length(30,true) */
    val taskName: Column[String] = column[String]("task_name", O.Length(30,varying=true))
    /** Database column project_id DBType(INTEGER) */
    val projectId: Column[Int] = column[Int]("project_id")
    /** Database column created_by DBType(INTEGER) */
    val createdBy: Column[Int] = column[Int]("created_by")
    /** Database column assigned_to DBType(INTEGER) */
    val assignedTo: Column[Int] = column[Int]("assigned_to")
    /** Database column created DBType(TIMESTAMP) */
    val created: Column[Option[DateTime]] = column[Option[DateTime]]("created")
    /** Database column updated DBType(TIMESTAMP) */
    val updated: Column[Option[DateTime]] = column[Option[DateTime]]("updated")
    /** Database column percentage_complete DBType(DECIMAL) */
    val percentageComplete: Column[Option[scala.math.BigDecimal]] = column[Option[scala.math.BigDecimal]]("percentage_complete")

    /** Primary key of Task (database name CONSTRAINT_3) */
    val pk = primaryKey("CONSTRAINT_3", (taskName, projectId))

    /** Foreign key referencing Project (database name CONSTRAINT_363) */
    lazy val projectFk = foreignKey("CONSTRAINT_363", projectId, ProjectTable)(r => r.id.get, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
    /** Foreign key referencing User (database name CONSTRAINT_3635) */
    lazy val userFk2 = foreignKey("CONSTRAINT_3635", createdBy, UserTable)(r => r.id.get, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
    /** Foreign key referencing User (database name CONSTRAINT_36358) */
    lazy val userFk3 = foreignKey("CONSTRAINT_36358", assignedTo, UserTable)(r => r.id.get, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table Task */
  lazy val TaskTable = new TableQuery(tag => new TaskTableDescription(tag))

  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Option[Int]], e1: GR[Int], e2: GR[Option[String]]): GR[User] = GR{
    prs => import prs._
      User.tupled((<<?[Int], <<[String], <<?[String], <<?[String]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class UserTableDescription(_tableTag: Tag) extends Table[User](_tableTag, "user") {
    def * = (id, handle, firstName, lastName) <> (User.tupled, User.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id, handle.?, firstName, lastName).shaped.<>({r=>import r._; _2.map(_=> User.tupled((_1, _2.get, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INTEGER) */
    val id: Column[Option[Int]] = column[Option[Int]]("id")
    /** Database column user_id DBType(INTEGER), PrimaryKey */
    val handle: Column[String] = column[String]("handle", O.PrimaryKey)
    /** Database column first_name DBType(VARCHAR), Length(50,true) */
    val firstName: Column[Option[String]] = column[Option[String]]("first_name", O.Length(50,varying=true))
    /** Database column last_name DBType(VARCHAR), Length(30,true) */
    val lastName: Column[Option[String]] = column[Option[String]]("last_name", O.Length(30,varying=true))

    /** Uniqueness Index over (id) (database name CONSTRAINT_INDEX_3) */
    val index1 = index("CONSTRAINT_INDEX_3", id, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val UserTable = new TableQuery(tag => new UserTableDescription(tag))
  
}
