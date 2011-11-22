package code.util

import java.text.SimpleDateFormat
import net.liftweb.http._
import java.text.ParseException
import java.util.Date
import java.util.Calendar

object DateUtil {
  
	def parse(str: String) = { 
	  try {
	    new SimpleDateFormat(S ?? "date.format").parse(str)
	  } catch {
	  	case e:ParseException => null 
	  }
	}
	
	def format(date: Date) = {
	  new SimpleDateFormat(S ?? "date.format").format(date)
	}
	
	def formatTime(date: Date) = {
	  new SimpleDateFormat(S ?? "time.format").format(date)
	}
	
	def parseSqlDate(str: String) = {
	  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(str)
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