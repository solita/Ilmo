package code.snippet
import code.comet.DataCenter
import net.liftweb.http.{S, RequestVar, SHtml}
import net.liftweb.http.js.JsCmds
import scala.xml.Text
import code.comet.RegisterMsg

class SignIn {
  
    object name extends RequestVar[String](S.param("name") openOr "")
  
    def render = {
      println("name in request is " + name.is)
      println("name in session is " + DataCenter.getName)

      if ( "" != name.is) {
          println("set name to session")
          //DataCenter ! RegisterMsg(name.is) does not work, why??
          DataCenter.setName(name.is)
          println("new name in session is " + DataCenter.getName())
      }
      
      <span></span>
    }
    
}