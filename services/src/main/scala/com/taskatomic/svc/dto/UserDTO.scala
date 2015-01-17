package com.taskatomic.svc.dto

import com.taskatomic.svc.model.{User, Project}

case class UserDTO(user: User, projects: List[Project])
