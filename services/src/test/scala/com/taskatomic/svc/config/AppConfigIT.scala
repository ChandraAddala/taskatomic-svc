package com.taskatomic.svc.config


import org.scalatest.FunSuite

class AppConfigIT extends FunSuite {

  test ("Verify existence of property") {
    assert(AppConfig.config.hasPath("appConfig.http.port"), "Property value for appConfig.http.port does not exist")
  }

  test ("Verify that property in classpath file will be returned if its not defined in external file") {

    //creating empty external conf file
    val t = java.io.File.createTempFile("taskatomic-services", ".conf")
    System.setProperty("config.file", t.getAbsolutePath)

    assert(AppConfig.config.hasPath("appConfig.http.port"), "Property value for appConfig.http.port does not exist")
  }

  test ("Verify retrieval of properties from test taskatomic-services.conf file") {
    val originalConfig = AppConfig.config
    val testConfig = originalConfig
      .getConfig("test")
      .withFallback(originalConfig)

    assert(testConfig.getInt("appConfig.http.port") === 7070, "Property value for appConfig.http.port from test config is invalid")
  }
}

