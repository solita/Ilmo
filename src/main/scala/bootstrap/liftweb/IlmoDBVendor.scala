package bootstrap.liftweb
import net.liftweb.db.StandardDBVendor
import net.liftweb.common.Box

//objenet.liftweb.common.Box

//object DBVendor extends ConnectionManager {
//  def newConnection(name: ConnectionIdentifier): Can[Connection] = {
//    try {
//      Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
//      val dm = DriverManager.getConnection("jdbc:derby:quepasa;create=true")
//      Full(dm)
//    } catch {
//      case e : Exception => e.printStackTrace; Empty
//    }
//  }
//  def releaseConnection(conn: Connection) {conn.close}
//}
 

class IlmoDBVendor(driverName : String, dbUrl : String, dbUser : Box[String], dbPassword : Box[String]) extends 
    StandardDBVendor(driverName : String, dbUrl : String, dbUser : Box[String], dbPassword : Box[String])  {
  
  override def maxPoolSize = 20;
  override def doNotExpandBeyond = 20

}
