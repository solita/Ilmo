package code.snippet
import code.comet.DataCenter
import net.liftweb.http.{S, RequestVar}
import scala.xml.Text
import net.liftweb.util.Helpers

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

        if ( hasNameCookie && !DataCenter.hasCurrentUserName ) {
          DataCenter setCurrentUserName( getNameCookie )
        }

        Text("")
    }
    
    private def hasNameCookie = S.findCookie("ilmo.name").isDefined
    private def getNameCookie = Helpers.urlDecode(S.findCookie("ilmo.name").get.value.get)
    
}