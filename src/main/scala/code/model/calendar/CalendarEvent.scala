package code.model.calendar
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Represents a VEVENT in the iCalendar fileformat.
 * UID can be used for cancelling event. It is required by Outlook.
 * Summary corresponds to subject in Outlook. 
 */
class CalendarEvent(uid: String, 
					startTime: Date, endTime: Date, 
                    summary: String ) {
  
  val format = new SimpleDateFormat("yyyyMMdd'T'HHmmss")
  
  val properties = List(
      ("DTSTAMP", format.format(startTime)), // Outlook requires DSTAMP
      ("UID", uid),
      ("SUMMARY", summary),
      ("DTSTART;TZID=Finland/Helsinki", format.format(startTime)),
      ("DTEND;TZID=Finland/Helsinki", format.format(endTime))
  )
  
  def propsStr = properties.map { option => option._1 + ":" + option._2 }
                           .reduceLeft { (option1, option2) => option1 + "\n" + option2 }
                           
  override def toString() : String = {
      "BEGIN:VEVENT" + 
      "\n" + propsStr + "\n" +
      "END:VEVENT"
  }
}
