package code

import org.specs.SpecificationWithJUnit
import org.joda.time.DateTime
import code.model.calendar.CalendarEvent
import code.model.calendar.Calendar
import java.util.Date

class CalendarTest extends SpecificationWithJUnit {

  "Calendars" should {  
    "start and end with vcalendar tags" in {
      val startTime = new Date();
      val endTime = new Date();
    
      val event = new CalendarEvent("uid", startTime, endTime, "testi", "paikka", "kuvaus", "jarjestaja", "jarjestaja@example.com");
      val calendar = new Calendar( event :: Nil );
      
      println(calendar.toString())
      
      calendar.toString() must startWith ("BEGIN:VCALENDAR") and endWith("END:VCALENDAR")
    }
  }
  
  "Calendar events" should {  
    "start and end with vevent tags" in {
      val startTime = new Date();
      val endTime = new Date();
    
      val event = new CalendarEvent("uid", startTime, endTime, "event summary", "paikka", "kuvaus", "jarjestaja", "jarjestaja@example.com");
      
      event.toString() must startWith ("BEGIN:VEVENT") and endWith("END:VEVENT")
    }
  }
}