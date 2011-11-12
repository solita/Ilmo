package code.util

import java.text.SimpleDateFormat
import net.liftweb.http._
import java.text.ParseException
import java.util.Date

object DateUtil {
  
	def parse(str:String) = { 
	  try {
	    new SimpleDateFormat(S ?? "date.format").parse(str)
	  } catch {
	  	case e:ParseException => null 
	  }
	}
	
	def format(date: Date) = {
	  new SimpleDateFormat(S ?? "date.format").format(date)
	}
	
	def parseSqlDate(str:String) = {
	  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(str)
	}
}