package code.model.calendar

class CalendarAlarm {
  
  val properties = List(
      //("X-WR-ALARMUID", "53BA9640-1028-46FB-B4DD-781EEE204B38"),
      ("TRIGGER", "-PT15M"),
      ("DESCRIPTION", "Sisko"),
      ("ACTION", "DISPLAY"))
  
  def propsStr = properties.map { prop => prop._1 + ":" + prop._2 }
                           .reduceLeft { (option1, option2) => option1 + "\n" + option2}
                           
  override def toString() : String = {
      "BEGIN:VALARM" + 
      "\n" + propsStr + "\n" +
      "END:VALARM"
  }
  
}