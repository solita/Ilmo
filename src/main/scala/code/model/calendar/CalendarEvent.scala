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
                    summary: String, location: String, description: String,
                    organizer: String, organizerEmail: String) {
  
  val format = new SimpleDateFormat("yyyyMMdd'T'HHmmss")
  
  val properties = //List(
      ("DTSTAMP", format.format(startTime)) :: // Outlook requires DSTAMP
      ("UID", uid) ::
      ("SUMMARY", textFormat(summary)) ::
      ("DTSTART;TZID=Finland/Helsinki", format.format(startTime)) ::
      ("DTEND;TZID=Finland/Helsinki", format.format(endTime)) ::
      ("LOCATION", textFormat(location)) ::
      ("DESCRIPTION", textFormat(description)) ::
      (if(organizerEmail != null && organizerEmail != "" && organizer != null && organizer != "") {
        ("ORGANIZER;CN=" + organizer + ":MAILTO", organizerEmail) :: Nil
      } else Nil)
  //)
  
  def textFormat(text: String) = {
	  text.replaceAll("\r", "")
      	  .replaceAll("\n", "\\\\n")
  }
  
  def propsStr = properties.map { option => option._1 + ":" + option._2 }
                           .reduceLeft { (option1, option2) => option1 + "\n" + option2 }
                           
  override def toString() : String = {
      "BEGIN:VEVENT" + 
      "\n" + propsStr + "\n" +
      "END:VEVENT"
  }
}
