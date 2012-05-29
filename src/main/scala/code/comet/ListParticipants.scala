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
import net.liftweb.http.js.JE.Call
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

case class TrainingDetails(name: String, place: String, date: String, desc: String, organizer: String,
                           materialLink: String, maxparticipants: Int, participants: List[String],
                           mailtoParticipantsHref: String)
                           
class ListParticipants extends CometActor with CometListener {
  
  implicit val formats = DefaultFormats
  
  private var selectedTraining: Box[Long] = Empty
  
  def registerWith = DataCenter
  
  def isSelected(trainingSessionId: Long) = Full(trainingSessionId) == selectedTraining
  
  def newTrainingSelected = 
    DataCenter.getSelectedTrainingSession != selectedTraining
  
  def setTraining = selectedTraining = DataCenter.getSelectedTrainingSession

  override def lowPriority = {
    case TrainingSelected(tId) if newTrainingSelected => setTraining; updateTrainingUI()
    case NewParticipant(pname, tId) if isSelected(tId) => updateTrainingUI()
    case DelParticipant(pname, tId) if isSelected(tId) => updateTrainingUI()
    // todo viesteja pitais hienojakoistaa: trainingadded, trainingsessionadded, ..edited, ..
    case TrainingsChanged => updateTrainingUI()
    case msg if ilmomsg(msg) => Noop
  }
  
  
  def updateTrainingUI(): JsCmd = {
    (for {
      trainingSessionId <- DataCenter.getSelectedTrainingSession 
      trainingSession <- TrainingSession.findByKey(trainingSessionId)
      training <- Training.findByKey(trainingSession.training)
      participants <- Full(trainingSession.participants.toList.map(_.name.is))
    }
    yield 
      partialUpdate(
          // todo builder-pattern ja enkoodaus utf8?
          Call("showTrainingDetails", write(TrainingDetails(training.name.is, trainingSession.place.is,
              interval(trainingSession), training.description.is, training.organizer.is,
              training.linkToMaterial.is, trainingSession.maxParticipants, participants,
              getMailtoHref(participants, training.name.is, trainingSession))))
      )
    )
    match {
      case Full(partialupdate) => partialupdate
      case _ => Noop
    }
  }
  
  override def render = {
    if ( !DataCenter.getSelectedTrainingSession.isEmpty ) updateTrainingUI() else Noop
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
  
  def interval(session: TrainingSession): String = 
    DateUtil.formatInterval(session.date.is, session.endDate.is)
  
}

