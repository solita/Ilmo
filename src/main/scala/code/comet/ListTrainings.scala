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
import java.util.Calendar



class ListTrainings extends CometActor with CometListener {
  
  private var showTrainingsSinceMonths = 3;
  
  def registerWith = DataCenter

  override def lowPriority = {
    case UserSignedIn(name) => reRender
    case NewParticipant(name, tId) => reRender
    case DelParticipant(name, tId) => reRender
    case TrainingsChanged => reRender
    case msg if msg.isInstanceOf[StateChanged] => Noop
  }

  override def render = {
    "#monthsback" #> getTrainingsSinceMonthsLinks &
    ".training *" #> getTrainingList.map(training => 
      ".name *" #> SHtml.a(() => viewDetails(training.id), Text(training.name)) &
      ".place *" #> training.place &
      ".date *" #> DateUtil.formatInterval(training.date, training.endDate) &
      ".participantCount" #> training.participantCount &
      ".maxParticipants" #> training.maxParticipants &
      ".register *" #> ( getRegisterButton(training) ) &
      ".addtocalendar *" #> <a title={S ?? "add.to.calendar"} href={"api/cal/"+training.id}>
                                <img src="/images/Calendar-Add-16.png" /></a>
    )
  }
    
  private def getTrainingsSinceMonthsLinks = {
    def textFor(n: Int) = {
      val title = if (n == 0) S ?? "show.trainings.since.now"
                  else (S ?? "show.trainings.since").format(n)
      
      if (showTrainingsSinceMonths!=n) 
        SHtml.a(() => {showTrainingsSinceMonths = n; reRender}, 
               <span title={title} class="selected">{n}</span>)  
      else 
        <span title={title}>{n}</span>
    }
    List(0,3,6,12,24).map(textFor _).reduceLeft[NodeSeq](_++_)
  }
  
  private def getTrainingList = {
    if ( DataCenter hasCurrentUserName ) 
      TrainingSession.getWithParticipantCountForParticipantId(
          DataCenter getCurrentUserName(), trainingsSinceDate) 
    else 
      TrainingSession.getWithParticipantCount(trainingsSinceDate)
  }
  
  private def trainingsSinceDate = {
    var fromDate = Calendar.getInstance()
    fromDate.add(Calendar.MONTH, -showTrainingsSinceMonths)
    fromDate.getTime()
  }
  
  def getRegisterButton(training: TrainingSessionParticipantCountDto) = {
    if ( training.date.before(new Date) ) {
      Text(S ?? "past.training")
    }
    else if ( training.hasSignedInUserParticipated ) {
      SHtml.ajaxButton(S ?? "training.unregister", () => unregister(training.id))
    }
    else {
      if ( training.participantCount >= training.maxParticipants ) { 
        Text(S ?? "training.full")
      }
      else if ( !DataCenter.hasCurrentUserName() ) {
        <button type="button" disabled="disabled">{S ?? "training.register"}</button>  
      }
      else {
        SHtml.ajaxButton(S ?? "training.register", () => register(training.id))
      }
    }
  }
      
  def register(trainingId: Long) : JsCmd = {
      DataCenter ! NewParticipant(DataCenter getCurrentUserName, trainingId)
  }

  def unregister(trainingId: Long) : JsCmd = {
      DataCenter ! DelParticipant(DataCenter getCurrentUserName, trainingId)
  }
  
  def viewDetails(trainingSessionId: Long) : JsCmd = {
      DataCenter.setSelectedTrainingSession(trainingSessionId)
  }

}

