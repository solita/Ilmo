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
import code.model.Training
import net.liftweb.http.js.JsCmd



class ListTrainings extends CometActor with CometListener {
  
  def registerWith = DataCenter
  
  override def lowPriority = {
    case NewParticipant(name: String, trainingId: Long) => {  
        println(name ++ " registered")
        reRender
        //partialUpdate(SetHtml("metrics", buildMetricList(metrics)))
    }
    case _ => reRender
  }
  
  override def render = {
    
    ".training *" #> Training.getWithParticipantCount.map(training => 
      ".name" #> training.name &
      ".participantCount" #> training.participantCount &
      ".viewdetails" #> ( SHtml.ajaxButton(S ?? "training.viewdetails", 
                          () => viewDetails(training.id, training.participantCount) )) &
      ".register" #> ( getRegisterButton(training.id, training.participantCount) )
    )
  }
  
  def getRegisterButton(trainingId: Long, participantCount: Long) = {
    if ( false ) { // training is full
      Text(S ?? "training.full")
    }    
    else if ( DataCenter.hasSignInName() ) {
      SHtml.ajaxButton(S ?? "training.register", () => register(trainingId, participantCount))
    }
    else {
      Text("-")
    }
      
  }
  
  def register(trainingId: Long, participantCount: Long) : JsCmd = {
      DataCenter ! NewParticipant(DataCenter.getName(), trainingId)
      Noop
  }
  
  def viewDetails(trainingId: Long, participantCount: Long) : JsCmd = {
    DataCenter.setSelectedTraining(trainingId)     
  }

}

