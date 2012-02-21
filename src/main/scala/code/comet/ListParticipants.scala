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
import scala.xml.Attribute
import scala.xml.Null
import code.util.DateUtil
import DataCenter._
import scala.collection.mutable.Buffer


class ListParticipants extends CometActor with CometListener {
  
  private var selectedTraining: Box[Long] = Empty
  
  def registerWith = DataCenter
  
  def isSelected(trainingSessionId: Long) = Full(trainingSessionId) == selectedTraining
  
  def newTrainingSelected = 
    DataCenter.getSelectedTrainingSession != selectedTraining
  
  def setTraining = selectedTraining = DataCenter.getSelectedTrainingSession

  override def lowPriority = {
    case TrainingSelected(tId) if newTrainingSelected => setTraining; reRender
    case NewParticipant(pname, tId) if isSelected(tId) => reRender
    case DelParticipant(pname, tId) if isSelected(tId) => reRender
    case TrainingsChanged => reRender
    case msg if ilmomsg(msg) => Noop
  }
  
  
  override def render = {
    (for {
      trainingSessionId <- DataCenter.getSelectedTrainingSession 
      trainingSession <- TrainingSession.findByKey(trainingSessionId)
      training <- Training.findByKey(trainingSession.training)
      participants <- Full(trainingSession.participants.map(_.name.is))
    }
    yield 
      "#trainingname *" #> training.name.is &
      "#trainingplace *" #> formatPlace(trainingSession) &
      "#trainingdesc *" #> formatText(training.description.is) &
      "#trainingorganizer *" #> formatTrainingOrganizer(training.organizer.is, training.organizerEmail.is) &
      "#traininglink *" #> formatLink(training.linkToMaterial.is) &
      ".participant *" #> participants &
      "#emailParticipantsLink [href]" #> getMailtoHref(participants, training.name.is,
                                                       trainingSession)
    )
    match {
      case Full(cssbindfunc) => cssbindfunc
      case _ => <span></span>
    }
  }
  
  def getMailtoHref(participants: Seq[String], trainingName: String,
                    trainingSession: TrainingSession): String = {
    "mailto:" + getMailAddressList(participants) +
    "?subject=" + trainingName + " " + interval(trainingSession) +
    "&body=" + "Hyvä osallistuja!"
  }
  
  def getMailAddressList(participantNames: Seq[String]): String = {
    participantNames
      .map(_.toLowerCase())
      .map(_.replace(" ", "."))
      .map(_.replace("ä", "a"))
      .map(_.replace("ö", "o"))
      .map(_+"@solita.fi")
      .mkString(";")
  } 
  
  def formatPlace(session: TrainingSession) = {
    session.place.is + " " + interval(session)
  }
  
  def interval(session: TrainingSession): String = 
    DateUtil.formatInterval(session.date.is, session.endDate.is)
  
  
  def formatTrainingOrganizer(organizer: String, email: String) = {
    organizer + (if(email != null && email != "") " ( " + email + " )" else "");
  }
  
  def formatLink(text: String): NodeSeq = {
    if(text != null && text != "") {
      var link = text
      if(!link.startsWith("http://")) link = "http://" + link
      <a>{text}</a> % Attribute(None, "href", Text(link), Null)
    } else Text("-");
  }
  
  def formatText(text: String): NodeSeq = {
    if (text == null) { 
      Text("-")
    } else {
      text.split("\n").map(t => Text(t): NodeSeq).reduceLeft((a,b) => a ++ <br/> ++ b)
    }
  }
  
}

