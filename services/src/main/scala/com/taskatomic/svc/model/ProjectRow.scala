package com.taskatomic.svc.model

import org.joda.time.DateTime

/** Entity class storing rows of table Project
  *  @param id Database column id DBType(INTEGER)
  *  @param projectName Database column project_name DBType(VARCHAR), Length(20,true)
  *  @param projectOwner Database column project_owner DBType(INTEGER)
  *  @param created Database column created DBType(TIMESTAMP)
  *  @param updated Database column updated DBType(TIMESTAMP) */
case class ProjectRow(id: Option[Int], 
                      projectName: String, 
                      projectOwner: Int, 
                      created: Option[DateTime], 
                      updated: Option[DateTime])
