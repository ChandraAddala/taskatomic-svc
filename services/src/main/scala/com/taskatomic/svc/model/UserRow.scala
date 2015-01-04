package com.taskatomic.svc.model

/** Entity class storing rows of table User
  *  @param id Database column id DBType(INTEGER)
  *  @param handle Database column user_id DBType(INTEGER), PrimaryKey
  *  @param firstName Database column first_name DBType(VARCHAR), Length(50,true)
  *  @param lastName Database column last_name DBType(VARCHAR), Length(30,true) */
case class UserRow(id: Option[Int], 
                   handle: String,
                   firstName: Option[String], 
                   lastName: Option[String])

