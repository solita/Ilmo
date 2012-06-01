package code.snippet

import net.liftweb._
import http._
import common._
import util._
import js._
import JsCmds._
import JE._
import scala.xml.NodeSeq
import code.model.TrainingSession
import code.model.Training
import java.text.SimpleDateFormat

object SessionCalendar extends DispatchSnippet {
  val dispatch = Map("render" -> buildFuncs _)

  def buildFuncs(in: NodeSeq): NodeSeq =
  	Script(SessionCalendarHandler.is.jsCmd &
         Function("getSessions", List("callback"), SessionCalendarHandler.is.call("getSessions", JsVar("callback"), JsObj()))
  )
}

object SessionFinder {
 
  def getSessionJsArray(): JsArray = {
    val trainings = TrainingSession.findAll()
    JsArray(trainings.map(t => {
      var training = Training.findByKey(t.training.is).get
      JsObj(("name", training.name.get), ("date",  new SimpleDateFormat("yyyy-MM-dd").format(t.date.get)))
    }))
  }
  
}

object SessionCalendarHandler extends SessionVar[JsonHandler] (
    
  new JsonHandler {
    def apply(in: Any): JsCmd = in match {
      case JsonCmd("getSessions", resp, _, _) => Call(resp, SessionFinder.getSessionJsArray())
      case _ => Noop
    }
  }
);