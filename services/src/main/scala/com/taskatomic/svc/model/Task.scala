package com.taskatomic.svc.model

import org.joda.time.DateTime

/** Entity class storing rows of table Task
  *  @param id Database column id DBType(INTEGER)
  *  @param taskName Database column task_name DBType(VARCHAR), Length(30,true)
  *  @param projectId Database column project_id DBType(INTEGER)
  *  @param createdBy Database column created_by DBType(INTEGER)
  *  @param assignedTo Database column assigned_to DBType(INTEGER)
  *  @param created Database column created DBType(TIMESTAMP)
  *  @param updated Database column updated DBType(TIMESTAMP)
  *  @param percentageComplete Database column percentage_complete DBType(DECIMAL) */
case class Task(id: Option[Int],
                   taskName: String, 
                   projectId: Int, 
                   createdBy: Int, 
                   assignedTo: Int, 
                   created: Option[DateTime], 
                   updated: Option[DateTime], 
                   percentageComplete: Option[scala.math.BigDecimal])