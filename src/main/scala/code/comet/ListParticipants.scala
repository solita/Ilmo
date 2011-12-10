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



class ListParticipants extends CometActor with CometListener {
  
  private var selectedTraining: Box[Long] = Empty
  
  def registerWith = DataCenter
  
  def newTrainingSelected = 
    DataCenter.getSelectedTrainingSession != selectedTraining
    
  override def lowPriority = {
    case StateChanged if newTrainingSelected => reRender
  }
  
  override def render = {
    (for {
      trainingSessionId <- DataCenter.getSelectedTrainingSession 
      trainingSession <- TrainingSession.findByKey(trainingSessionId)
      training <- Training.findByKey(trainingSession.training)
      participants <- Full(trainingSession.participants.map(_.name).zipWithIndex)
    }
    yield 
      "#trainingname *" #> training.name.is &
      "#trainingdesc *" #> formatText(training.description.is) &
      "#trainingorganizer *" #> formatTrainingOrganizer(training.organizer.is, training.organizerEmail.is) &
      "#traininglink *" #> formatLink(training.linkToMaterial.is) &
      "#trainingother *" #> formatText(training.other.is) &
      ".participant" #> participants.map(p => (".participant [class]" #> (("col" + p._2%3)) & ".name *" #> p._1))
    )
    match {
      case Full(cssbindfunc) => cssbindfunc
      case _ => <span></span>
    }
  }
  
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
    text.split("\n").map(t => Text(t): NodeSeq).reduceLeft((a,b) => a ++ <br/> ++ b)
  }
  
}

