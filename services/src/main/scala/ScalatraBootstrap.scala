import javax.servlet.ServletContext

import com.mchange.v2.c3p0.{ComboPooledDataSource, PooledDataSource}
import com.taskatomic.svc.controller.AppController
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory

import scala.slick.jdbc.JdbcBackend._

class ScalatraBootstrap extends LifeCycle{

  val logger = LoggerFactory.getLogger(getClass)

  var pooledDataSource: PooledDataSource = null
  
  override def init(context: ServletContext) {
    super.init(context)
    
    logger.info("Initializing c3p0 connection pool")
    pooledDataSource = new ComboPooledDataSource
    val db = Database.forDataSource(pooledDataSource)
    
    context.mount(new AppController(db), "/*")
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    
    logger.info("Closing c3p0 connection pool")
    pooledDataSource.close
  }
}
