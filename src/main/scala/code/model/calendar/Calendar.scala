package code.model.calendar

class Calendar(events: List[CalendarEvent]) {
  val properties = List(
      ("PRODID", "-//Ilmo//FI"),
      ("VERSION", "2.0"),
      ("CALSCALE", "GREGORIAN"))
  
  def propsStr = properties.map { option => option._1 + ":" + option._2 }
                           .reduceLeft { (option1, option2) => option1 + "\n" + option2}
                           
  var eventStr = events.map(_.toString)
                       .reduceLeft { (e1, e2) => e1 + "\n" + e2 }
  
  
  override def toString() : String = {
      "BEGIN:VCALENDAR" + 
      "\n" + propsStr + "\n" +
      eventStr + "\n" + 
      "END:VCALENDAR"
  }
  
}