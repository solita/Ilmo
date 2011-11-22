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
      ".date" #> formatDate(training.date, training.endDate) &
      ".participantCount" #> training.participantCount &
      ".maxParticipants" #> training.maxParticipants &
      ".viewdetails" #> ( SHtml.ajaxButton(S ?? "training.viewdetails", () => viewDetails(training.id) )) &
      ".register" #> ( getRegisterButton(training) ) &
      ".addtocalendar" #> <a href={"api/cal/"+training.id}>{S ?? "add.to.calendar"}</a>
    )
  }
  
  def formatDate(date: Date, endDate: Date) = {
    DateUtil.format(date) + " - " + 
     (if(DateUtil.isSameDay(date,endDate)) DateUtil.formatTime(endDate) else DateUtil.format(endDate)) 
    
  }
  
  def getRegisterButton(training: TrainingSessionParticipantCountDto) = {
    if ( training.date.before(new Date) ) {
      Text("-")
    }
    else if ( training.hasSignedInUserParticipated ) {
      SHtml.ajaxButton(S ?? "training.unregister", () => unregister(training.id))
    }
    else {
      if ( training.participantCount >= training.maxParticipants ) { 
        Text(S ?? "training.full")
      }
      else if ( !DataCenter.hasSignInName() ) {
        Text("-")  
      }
      else {
        SHtml.ajaxButton(S ?? "training.register", () => register(training.id))
      }
    }
  }
  
  def register(trainingId: Long) : JsCmd = {
      DataCenter ! NewParticipant(DataCenter.getName(), trainingId)
      Noop
  }

  def unregister(trainingId: Long) : JsCmd = {
      DataCenter ! DelParticipant(DataCenter.getName(), trainingId)
      Noop
  }
  
  def viewDetails(trainingSessionId: Long) : JsCmd = {
    DataCenter.setSelectedTrainingSession(trainingSessionId)     
  }

}

