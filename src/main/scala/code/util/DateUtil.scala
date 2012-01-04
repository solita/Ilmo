package code.util

import java.text.SimpleDateFormat
import net.liftweb.http._
import java.text.ParseException
import java.util.Date
import java.util.Calendar
import org.joda.time.DateTime

object DateUtil {
  
  def main(args: Array[String]) = {
    val d2 = new Date();
    d2.setHours(12);
    d2.setMinutes(0);
    d2.setSeconds(0);
    
    val d = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(d2)
    println(d)  
  }
  
  def parseDate(str: String) = 
    new SimpleDateFormat(S ?? "date.format").parse(str)

  def parseTime(str: String) = 
    new SimpleDateFormat(S ?? "time.format").parse(str)
    
    
	def parseDateTime(str: String) = { 
	  try {
	    new SimpleDateFormat(S ?? "datetime.format").parse(str)
	  } catch {
	  	case e:ParseException => null 
	  }
	}
	
	def formatDateTime(date: Date) = {
	  new SimpleDateFormat(S ?? "datetime.format").format(date)
	}
	
	def format(date: Date) = {
    new SimpleDateFormat(S ?? "date.format").format(date)
  }
	
	def formatTime(date: Date) = {
	  new SimpleDateFormat(S ?? "time.format").format(date)
	}
	
	def formatInterval(startdate: Date, endDate: Date) = {
	  println("formatting: " + startdate);
	  formatDateTime(startdate) + " - " + 
     (if ( isSameDay(startdate, endDate) ) 
       formatTime(endDate) 
     else 
       formatDateTime(endDate)) 
  }
	
	def parseSqlDate(str: String) = {
	  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str)
	}
	
	def isSameDay(d1: Date, d2: Date): Boolean = {
	  val c1 = Calendar.getInstance
	  val c2 = Calendar.getInstance
	  c1.setTime(d1)
	  c2.setTime(d2)
	  List(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
	   .foldLeft(true)((b, f) => (b && (c1.get(f) == c2.get(f))))
	}
}