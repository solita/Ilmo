package code.model.calendar
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.Req
import net.liftweb.http.GetRequest
import net.liftweb.http.InMemoryResponse
import java.text.SimpleDateFormat
import org.joda.time.DateTime


object CalendarICSFileHelper extends RestHelper {

  // TODO this extractor is probably predefined somewhere, but where?
  object AsLong {
    def unapply(str: String) : Option[Long] = 
      try { Some(str.toLong) } catch { case _ => None }  
  }

  def getICSFileForTraining(trainingId: Long) : InMemoryResponse = {
    val response = "testi " + trainingId
    val startTime = new DateTime();
    val endTime = new DateTime();
    
    val calendar = new Calendar( 
        List( new CalendarEvent(trainingId.toString(), startTime, endTime, "testi") )
    )
        
    new InMemoryResponse(calendar.toString().getBytes("UTF-8"), 
        List("Content-Type" -> "text/calendar"), Nil, 200)
  }

  serve { 
    case Req("api" :: "cal" :: AsLong(id) :: _, _, GetRequest) => getICSFileForTraining(id)
  }
  
}