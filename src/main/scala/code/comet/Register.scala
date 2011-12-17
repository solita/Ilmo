package code.comet

import net.liftweb.http.{S, RequestVar, SHtml}
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.JsCmds.Noop
import scala.xml.Text
import DataCenter._

class Register extends CometActor with CometListener {

    def registerWith = DataCenter
  
    override def lowPriority = {
      case NewParticipant(pname, tId) => viewMsg(localizedText("new.participant.msg", pname))
      case DelParticipant(pname, tId) => viewMsg(localizedText("del.participant.msg", pname))
      case TrainingsChanged => viewMsg(localizedText("trainings.changed.msg"))
      case UserSignedIn(name) => updateWelcomeText
      case msg if msg.isInstanceOf[StateChanged] => Noop
    }
    
    def updateWelcomeText = {
      partialUpdate(SetHtml("welcome", getWelcomeTextOrAskNameForm))
      partialUpdate(SetHtml("signout", showSignoutLogoIfUserHasSignedIn))
    }
    
    def viewMsg(msg: Text) = partialUpdate(SetHtml("ilmomsg", msg)) 
    
    def localizedText(localizationKey: String, param: String*) = 
      if (param.length == 0) Text(S ?? localizationKey)
      else Text((S ?? localizationKey).format(param.head))
    
    override def render = {
      "#signout *" #> showSignoutLogoIfUserHasSignedIn &
      "#welcome *" #> getWelcomeTextOrAskNameForm &
      "#ilmomsg *" #> Text("")
    }
    
    def showSignoutLogoIfUserHasSignedIn = {
      if (hasCurrentUserName)
        SHtml.a(() => {clearUserName}, <img title="signout" src="/images/signout.png"/>)
      else 
        Text("")
    }
    
    def getWelcomeTextOrAskNameForm = {
      if (hasCurrentUserName) {
          localizedText("welcome", getCurrentUserName)
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
    
    def signin(name: String) = setCurrentUserName(name)
      
}