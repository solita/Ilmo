package bootstrap.liftweb
import net.liftweb.db.StandardDBVendor
import net.liftweb.common.Box

class IlmoDBVendor    (driverName : String, dbUrl : String, dbUser : Box[String], dbPassword : Box[String]) extends
      StandardDBVendor(driverName : String, dbUrl : String, dbUser : Box[String], dbPassword : Box[String])  {
  
  override def maxPoolSize = 30
  override def doNotExpandBeyond = 40

}
