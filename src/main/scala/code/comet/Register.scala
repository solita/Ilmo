package code.comet

import net.liftweb.http.{S, RequestVar, SHtml}
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.JsCmds.Noop
import scala.xml.Text

class Register extends CometActor with CometListener {

    def registerWith = DataCenter
  
    override def lowPriority = {
      case UserSignedIn(name) => reRender
      case msg if msg.isInstanceOf[StateChanged] => Noop
    }
    
    override def render = {
      if (DataCenter.hasCurrentUserName) {
          Text( (S ?? "welcome").format(DataCenter.getCurrentUserName()))
      }
      else {
          SHtml.ajaxForm(
             Text(S ?? "what.is.your.name") ++
             /* SHtml.text generates a text input that invokes a Scala
              * callback (in this case, the login method) with the text
              * it contains when the form is submitted. */
              SHtml.text("", signin) 
              ++ <input type="submit" value="Kirjaudu" />
          )
      }
    }
    
    def signin(name: String) {
      DataCenter.setCurrentUserName(name)
    }
      
}