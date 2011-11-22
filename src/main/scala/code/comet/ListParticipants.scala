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
      "#trainingdesc *" #> formatText(training.description.is) &
      "#trainingorganizer *" #> training.organizer.is &
      "#traininglink *" #> formatLink(training.linkToMaterial.is) &
      "#trainingother *" #> formatText(training.other.is) &
      ".participant *" #> trainingSession.participants.map(participant => ".name" #> participant.name.is)
    )
    match {
      case Full(cssbindfunc) => cssbindfunc
      case _ => <span></span>
    }
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

