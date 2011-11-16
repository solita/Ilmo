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
import code.model.{Training, TrainingSession}
import net.liftweb.http.js.JsCmd
import code.util.DateUtil
import code.model.TrainingSessionParticipantCountDto



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
    
    val trainingList = if ( DataCenter hasSignInName ) { 
      TrainingSession.getWithParticipantCountForParticipantId(DataCenter.getName()) 
    } else {
      TrainingSession.getWithParticipantCount
    }
    
    ".training *" #> trainingList.map(training => 
      ".name" #> training.name &
      ".place" #> training.place &
      ".date" #> DateUtil.format(training.date) &
      ".participantCount" #> training.participantCount &
      ".maxParticipants" #> training.maxParticipants &
      ".viewdetails" #> ( SHtml.ajaxButton(S ?? "training.viewdetails", () => viewDetails(training.id) )) &
      ".register" #> ( getRegisterButton(training) )
    )
  }
  
  def getRegisterButton(training: TrainingSessionParticipantCountDto) = {
    if ( training.participantCount >= training.maxParticipants ) { // training is full
      Text(S ?? "training.full")
    }    
    else if ( DataCenter.hasSignInName() && !training.hasSignedInUserParticipated ) {
      SHtml.ajaxButton(S ?? "training.register", () => register(training.id))
    }
    else {
      Text("-")
    }
      
  }
  
  def register(trainingId: Long) : JsCmd = {
      DataCenter ! NewParticipant(DataCenter.getName(), trainingId)
      Noop
  }
  
  def viewDetails(trainingSessionId: Long) : JsCmd = {
    DataCenter.setSelectedTrainingSession(trainingSessionId)     
  }

}

