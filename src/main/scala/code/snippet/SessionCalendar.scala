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
         Function("getSessions", List("callback", "year", "month"), 
             SessionCalendarHandler.is.call("getSessions", JsVar("callback"), JsObj(("year", JsVar("year")), ("month", JsVar("month")))))
  )
}

object SessionFinder {
 
  def getSessionJsArray(year: Int,  month: Int): JsArray = {
    val trainings = TrainingSession.getSummariesForMonth(year, month);
    JsArray(trainings.map(t => {
      JsObj(("name", t.name), ("date",  new SimpleDateFormat("yyyy-MM-dd").format(t.date)))
    }))
  }
  
}

object SessionCalendarHandler extends SessionVar[JsonHandler] (
    
  new JsonHandler {
    def apply(in: Any): JsCmd = in match {
      case JsonCmd("getSessions", resp, params: Map[String, String], _) => Call(resp, 
          SessionFinder.getSessionJsArray(params.get("year").get.toInt, params.get("month").get.toInt))
      case _ => Noop
    }
  }
);