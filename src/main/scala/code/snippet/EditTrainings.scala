package code.comet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import Helpers._
import util._
import Helpers._
import net.liftweb.http.js.JsCmds._
import _root_.scala.xml.Text
import scala.xml.NodeSeq
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.http.{S, SessionVar, SHtml}
import code.model.TrainingSession
import net.liftweb.http.js.JsCmd


class EditTrainings {
  
   def listTrainings = {
    
    ".training *" #>  TrainingSession.getWithParticipantCount.map(training => 
      ".name" #> training.name &
      ".participantCount" #> training.participantCount &
      ".remove" #> getRemoveButton(training.id, training.participantCount) &
      ".edit" #> getRemoveButton(training.id, training.participantCount)
    ) 
    
  }
   
   def getRemoveButton(trainingId: Long, participantCount: Long) = {
     // TODO disabloi, jos osallistujia?
     SHtml.ajaxButton(S ?? "training.remove", () => removeTraining(trainingId, participantCount))
  }
  
  def removeTraining(trainingId: Long, participantCount: Long) : JsCmd = {
      TrainingSession.findByKey(trainingId) match {
        case Full(training) => training.delete_!
        case _ => Nil
      }
      DataCenter !! TrainingDeleted
      Noop
  }  
  
}



