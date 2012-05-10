package code.snippet
import code.comet.DataCenter
import net.liftweb.http.{S, RequestVar, SHtml}
import net.liftweb.http.js.JsCmds
import scala.xml.Text
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.common.{Full, Empty}

// snipetti, joka ottaa kiinni pyynnön parametreja ja välittää ne eteenpäin
class SignIn {
  
    object name extends RequestVar[String](S.param("name") openOr "")  
    object firstname extends RequestVar[String](S.param("firstname") openOr "")
    object lastname extends RequestVar[String](S.param("lastname") openOr "")
    
    def render = {

        
        if ( "" != firstname.is && "" != lastname.is ) {
            DataCenter setCurrentUserName(firstname.is + " " + lastname.is)
        }
        
        else if ( "" != name.is ) {
            DataCenter setCurrentUserName(name.is)
        }

// todo: ei toimi ihan näin        
//        if ( !hasNameCookie && DataCenter.hasCurrentUserName ) {
//          S.addCookie(createNameCookie(DataCenter getCurrentUserName()))
//        }
        
        Text("")
    }
    
    private def hasNameCookie = S.findCookie("ilmo.name").isDefined
    
    private def createNameCookie(name: String): HTTPCookie = {
      val maxAge = Full(2629743)
      val version = Full(1)
      val secure = Empty
      val httpOnly = Full(true)
      
      HTTPCookie("ilmo.name", 
                 Full(name), 
                 Full(S.hostName), 
                 Full(S.contextPath),
                 maxAge,
                 version,
                 //secure,
                 httpOnly)
    }
}