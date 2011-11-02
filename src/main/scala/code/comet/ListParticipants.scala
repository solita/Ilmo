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



class ListParticipants extends CometActor with CometListener {
  
  def registerWith = DataCenter
  
  override def lowPriority = {
    case _ => reRender
  }
  
  override def render = {
    
    DataCenter.getSelectedTraining match {
      case Empty => <span></span>
      case Full(trainingId: Long) => {
    	  val training = Training.findByKey(trainingId) openOr Training.create
    	  
    	  "#trainingdesc" #> training.description.is &
    	  ".participant *" #> training.participants.map(participant => 
              ".name" #> participant.name.is)
      }
    }
  }
  
}

