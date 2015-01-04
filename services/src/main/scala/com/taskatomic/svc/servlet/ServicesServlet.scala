package com.taskatomic.svc.servlet

import com.taskatomic.svc.model.DBTables._
import com.taskatomic.svc.model.UserRow
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

class ServicesServlet(db: Database) extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }
  
  get("/") {
    "Taskatomic services"
  }
  
  get("/users/:user_handle") {
    val userHandle = params("user_handle")

    //TODO: Crazy stuff...Remove!!
    db withDynSession {
      User.map(user => (user.handle, user.firstName, user.lastName))
        .insert(userHandle, Some("Jason"), Some("Borne"))
    }
    
    db withDynSession {
      User.filter(_.handle === userHandle).run
    }
    
  }
}
