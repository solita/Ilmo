package code.snippet 

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import Helpers._
import code.model._
import js.JsCmds.SetHtml
import js.{JsCmd, JsCmds}
import net.liftweb.http.js.JE.JsRaw

class MainSnippet {
  
  object pname extends SessionVar[String]("")
    
  def viewParticipants(trainingId: Long, participantCount: Long) : JsCmd = {
    if (participantCount == 0)
      SetHtml("participant_table", Text(S ?? "training.no-participants"))
    else
      SetHtml("participant_table", buildParticipantTable(trainingId)) &
      SetHtml("signin", buildSignInForm(trainingId, participantCount))
  }

  def buildParticipantTable(trainingId: Long) : NodeSeq = {
    val training = Training.findByKey(trainingId) openOr Training.create
    
    println("training " + trainingId + " has " + training.participants.length )
    
    Training.findByKey(trainingId) match {
      case Full(training) => training.participants.flatMap(
          participant => <tr>
                           <td>{participant.name.is}</td>
                         </tr>)

      case _ => Text(S ?? "training.removed")
    }
  }
  
  def viewSigninForm(trainingId: Long, participantCount: Long) : JsCmd = {
    if (participantCount == -999) // todo if training is full
      SetHtml("signin", Text(S ?? "training.is-full"))
    else
      SetHtml("signin", buildSignInForm(trainingId, participantCount))
  }
       
  def buildSignInForm(trainingId: Long, participantCount: Long) : NodeSeq = {
//    SHtml.ajaxForm(
//        SHtml.text(pname, pname = _, "maxlength" -> "40"),
//        addParticipant(trainingId, participantCount)
//    )
    var name = ""
    val id = trainingId
    val count = participantCount

/*  <span>
    { SHtml.text("name", n => name = n) }
    { SHtml.submit("Submit", () => addParticipant2(name, id, count)) }
  </span>
*/
    /*
    SHtml.text(name, name = _, "maxlength" -> "40") ++
    SHtml.ajaxButton("ilmoittaudu", () => 
      {addParticipant(name, id, count)})
      */
    SHtml.text("", addParticipant2(_, id, count), "maxlength" -> "40") ++
    SHtml.ajaxButton(S ?? "training.register", () => S.redirectTo("/"))
  }
  
  def addParticipant(name: String, trainingId: Long, participantCount: Long) : JsCmd = {
    Training.findByKey(trainingId) match {
      case Full(training) => {
        Participant.create.name(name).training(training).save;
        S.redirectTo("/")
      }
      case Empty => SetHtml("signin", Text(S ?? "training.not-found"))
    }
  }
 
   def addParticipant2(name: String, trainingId: Long, participantCount: Long) {
    Training.findByKey(trainingId) match {
      case Full(training) => {
        Participant.create.name(pname.is).training(training).save
      }
    }
  }
   
  def listTrainings = {
    
    ".training *" #> Training.getWithParticipantCount.map(training => 
      ".name" #> training.name &
      ".participantCount" #> training.participantCount &
      ".viewdetails" #> ( SHtml.ajaxButton(S ?? "training.register", 
                          () => { viewParticipants(training.id, training.participantCount)
                                  //viewSigninForm(training.id, training.participantCount)
                                }
                        ))
                         

    )
  }
  
}
