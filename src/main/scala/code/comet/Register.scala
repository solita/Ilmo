package code.comet

import net.liftweb.http.{S, RequestVar, SHtml}
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.http.js.JsCmds.SetHtml

class Register extends CometActor with CometListener {

    def registerWith = DataCenter
  
    override def lowPriority = {
        case _ => {  
            //partialUpdate(SetHtml("signin", <b>{DataCenter.getName}</b>))
            reRender
        }
    }
    
    override def render = {
      println("name in session is " + DataCenter.getName)

      if (DataCenter.hasSignInName) {
          <span></span>
      }
      else {
          SHtml.ajaxForm(
             <span>Jos haluat ilmoittautua, anna nimesi? </span> ++
             /* SHtml.text generates a text input that invokes a Scala
              * callback (in this case, the login method) with the text
              * it contains when the form is submitted. */
              SHtml.text("", signin) 
              ++ <input type="submit" value="Kirjaudu" />
          )
      }
    }
    
    def signin(name: String) {
      DataCenter.setName(name)
      reRender
    }
      
}