package code.model.calendar
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.Req
import net.liftweb.http.GetRequest
import net.liftweb.http.InMemoryResponse
import code.model.TrainingSession
import net.liftweb.common.Box
import net.liftweb.http.BadResponse
import net.liftweb.util.Helpers._
import net.liftweb.http.LiftResponse

object CalendarICSFileHelper extends RestHelper {

  object AsTraining {
    def unapply(trainingSessionId: String) : Option[TrainingSession] = 
      tryo(trainingSessionId.toInt).flatMap(TrainingSession.findByKey(_))
  }

  private def createCalendar(trainingSession: TrainingSession): Box[Calendar] = {
    for {training <- trainingSession.training.obj}
    yield new Calendar( new CalendarEvent(trainingSession.id.is.toString, 
                                          trainingSession.date.is, 
                                          trainingSession.endDate.is, 
                                          training.name.is, 
                                          trainingSession.place,
                                          training.description.is,
                                          training.organizer.is, 
                                          training.organizerEmail.is) :: Nil )
  }
  
  def getICSFileForTraining(trainingSession: TrainingSession): LiftResponse = {
    
    tryo(InMemoryResponse(createCalendar(trainingSession).open_!.toString().getBytes("UTF-8"), 
                               List("Content-Type" -> "text/calendar"), 
                               Nil, 200)
    ) openOr BadResponse()
        
  }

  serve { 
    case Req("api" :: "cal" :: AsTraining(training) :: _, _, GetRequest) => getICSFileForTraining(training)
  }
  
}