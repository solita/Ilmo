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
      case UserSignedIn(name) if isMyUser(name) => handleSignin
      case UserSignedOut(name) if wasMyUser(name) && !hasCurrentUserName => handleSignout
      case msg if ilmomsg(msg) => Noop
    }
    
    private def handleSignout = {
      partialUpdate(SetHtml("welcome", askNameForm) & SetHtml("signout", Text("")))
    }
    
    private def handleSignin = {
      partialUpdate(SetHtml("welcome", welcomeText) & SetHtml("signout", signoutLink))
    }
    
    private def viewMsg(msg: Text) = partialUpdate(SetHtml("ilmomsg", msg)) 
        
    override def render = {
      "#signout *" #> (if (hasCurrentUserName) signoutLink else Text("")) &
      "#welcome *" #> (if (hasCurrentUserName) welcomeText else askNameForm) &
      "#ilmomsg *" #> Text("")
    }
    
    private def signoutLink = SHtml.a(() => {signout(getCurrentUserName)}, 
                <img title="signout" src="/images/signout.png"/>)
                
    private def welcomeText = localizedText("welcome", getCurrentUserName)
    
    private def askNameForm = SHtml.ajaxForm(
             localizedText("what.is.your.name") ++
              SHtml.text("", signin) ++ 
              <input type="submit" value="Kirjaudu" />)

    private def signin(name: String) = setCurrentUserName(name)
    
    private def localizedText(localizationKey: String, param: String*) = 
      if (param.length == 0) Text(S ?? localizationKey)
      else Text((S ?? localizationKey).format(param.head))

}