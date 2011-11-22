package code.model.calendar
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.Req
import net.liftweb.http.GetRequest
import net.liftweb.http.InMemoryResponse
import java.text.SimpleDateFormat
import org.joda.time.DateTime
import code.model.TrainingSession
import code.model.Training
import net.liftweb.common.Box
import net.liftweb.common.Full


object CalendarICSFileHelper extends RestHelper {

  // TODO this extractor is probably predefined somewhere, but where?
  object AsLong {
    def unapply(str: String) : Option[Long] = 
      try { Some(str.toLong) } catch { case _ => None }  
  }

  private def createCalendar(trainingSession: TrainingSession): Box[Calendar] = {
    for {training <- trainingSession.training.obj}
    yield new Calendar( new CalendarEvent(trainingSession.id.is.toString, 
                                          trainingSession.date.is, 
                                          trainingSession.endDate.is, 
                                          training.name.is, trainingSession.place,
                                          training.description.is) :: Nil )
  }
  
  def getICSFileForTraining(trainingSessionId: Long) : InMemoryResponse = {
    
    (for { trainingSession <- TrainingSession.findByKey(trainingSessionId)
          calendar <- createCalendar(trainingSession)
    } 
    yield new InMemoryResponse(calendar.toString().getBytes("UTF-8"), 
                               List("Content-Type" -> "text/calendar"), 
                               Nil, 200)
    ) openOr InMemoryResponse(Array(), Nil, Nil, 401)
        
  }

  serve { 
    case Req("api" :: "cal" :: AsLong(id) :: _, _, GetRequest) => getICSFileForTraining(id)
  }
  
}