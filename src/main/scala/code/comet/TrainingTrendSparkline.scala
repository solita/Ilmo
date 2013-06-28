package code.comet
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.http.js.JE.JsArray
import net.liftweb.http.js.JE.JsObj
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.http.js.JE.Num
import net.liftweb.http.js.JE.Call
import code.model.TrainingSession
import org.joda.time.DateTime
import DataCenter._

class TrainingTrendSparkline extends CometActor with CometListener {
  
    def registerWith = DataCenter
  
    override def lowPriority = {
      case TrainingsChanged => reRender
      case msg if ilmomsg(msg) => Noop
    }
    
    def getMonthlyParticipantCounts: JsArray = {
      val monthsback = 18
      val afterDate = new DateTime().minusMonths(monthsback).withDayOfMonth(1)
      val months = (0 to monthsback).map(afterDate.plusMonths)
      
      val participantCounts = TrainingSession.getMonthlyParticipantCount(afterDate.toDate)
      
      def getCountFor(y: Int, m: Int) = 
        participantCounts.filter(pc => pc.year == y && pc.month == m) match {
        case Nil => 0
        case stats => stats.head.count
      }
      
      val counts = for (m <- months) yield getCountFor(m.getYear, m.getMonthOfYear)
      JsArray(counts.toList.map(Num(_)))  
    }
    
    override def render = {
      val data = getMonthlyParticipantCounts
      
      val opts = JsObj(("zeroAxis" -> false),
                       ("type" -> "bar"),
                       ("barcolor" -> "#9999FF"),
                       ("zeroColor" -> "#EBEBFF"));
      
      Call("drawGraph", data, opts).cmd
    }
    
}