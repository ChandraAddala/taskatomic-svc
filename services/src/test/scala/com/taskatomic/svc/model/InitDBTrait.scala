package com.taskatomic.svc.model


import com.taskatomic.svc.model.DBTables._
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

trait InitDBTrait {
  
  
  def clearData(db: Database) {
    db withDynSession {
      Task.delete
      Project.delete
      User.delete
    }
  }
}
