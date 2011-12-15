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
      case NewParticipant(pname, tId) => viewMsg(localizedText("new.participant.msg", pname))
      case DelParticipant(pname, tId) => viewMsg(localizedText("del.participant.msg", pname))
      case TrainingsChanged => viewMsg(localizedText("trainings.changed.msg"))
      case UserSignedIn(name) => partialUpdate(SetHtml("welcome", getWelcomeTextOrAskNameForm))
      case msg if msg.isInstanceOf[StateChanged] => Noop
    }
    
    def viewMsg(msg: Text) = partialUpdate(SetHtml("ilmomsg", msg)) 
    
    def localizedText(localizationKey: String, param: String*) = 
      if (param.length == 0) Text(S ?? localizationKey)
      else Text((S ?? localizationKey).format(param.head))
    
    override def render = {
      "#welcome *" #> getWelcomeTextOrAskNameForm &
      "#ilmomsg *" #> Text("")
    }
    
    def getWelcomeTextOrAskNameForm = {
      if (DataCenter.hasCurrentUserName) {
          localizedText("welcome", DataCenter.getCurrentUserName())
      }
      else {
          SHtml.ajaxForm(
             localizedText("what.is.your.name") ++
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