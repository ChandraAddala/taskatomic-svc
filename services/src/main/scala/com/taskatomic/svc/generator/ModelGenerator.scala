object ModelGenerator extends App {
  
  Console.println("Generating model classes for taskatomic services")
  
  scala.slick.codegen.SourceCodeGenerator.main(
    Array("scala.slick.driver.H2Driver",
      "org.h2.Driver",
      "jdbc:h2:file:/Users/chan5120/Documents/workspacecf2/taskatomic-svc/services/build/db/test/taskatomic-db",
      "services/src/main/scala",
      "com.taskatomic.svc.model",
      "root",
      "")
  )
}
