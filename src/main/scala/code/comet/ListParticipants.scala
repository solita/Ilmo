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
import code.model.Training



class ListParticipants extends CometActor with CometListener {
  
  def registerWith = DataCenter
  
  override def lowPriority = {
    case _ => reRender
  }
  
  override def render = {
    
    (for {
      trainingSessionId <- DataCenter.getSelectedTrainingSession 
      trainingSession <- TrainingSession.findByKey(trainingSessionId)
      training <- Training.findByKey(trainingSession.training)
    }
    yield 
      "#trainingdesc" #> training.description.is &
      ".participant *" #> trainingSession.participants.map(participant => ".name" #> participant.name.is)
    ) 
    match {
      case Full(cssbindfunc) => cssbindfunc
      case _ => <span></span>
    }
    
//    
//    DataCenter.getSelectedTrainingSession match {
//      case Empty => <span></span>
//      case Full(trainingSessionId: Long) => {
//    	  val trainingSession = TrainingSession.findByKey(trainingSessionId).get
//    	  val training = Training.findByKey(trainingSession.training.get).get
//    	  
//    	  "#trainingdesc" #> training.description.is &
//    	  ".participant *" #> trainingSession.participants.map(participant => ".name" #> participant.name.is)
//      }
//    }
  }
  
}

