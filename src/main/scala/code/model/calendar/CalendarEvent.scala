package code.model.calendar
import org.joda.time.DateTime
import java.text.SimpleDateFormat

/**
 * Represents a VEVENT in the iCalendar fileformat.
 * UID can be used for cancelling event. It is required by Outlook.
 * Summary corresponds to subject in Outlook. 
 */
class CalendarEvent(uid: String, 
					startTime: DateTime, endTime: DateTime, 
                    summary: String ) {
  
  val format = new SimpleDateFormat("yyyyMMdd'T'HHmmss")
  
  val properties = List(
      ("DTSTAMP", format.format(startTime.toDate())), // Outlook requires DSTAMP
      ("UID", uid),
      ("SUMMARY", summary),
      ("DTSTART;TZID=Finland/Helsinki", format.format(startTime.toDate())),
      ("DTEND;TZID=Finland/Helsinki", format.format(endTime.toDate()))
  )
  
  def propsStr = properties.map { option => option._1 + ":" + option._2 }
                           .reduceLeft { (option1, option2) => option1 + "\n" + option2 }
                           
  override def toString() : String = {
      "BEGIN:VEVENT" + 
      "\n" + propsStr + "\n" +
      "END:VEVENT"
  }
}
