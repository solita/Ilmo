package code.util

import java.text.SimpleDateFormat
import net.liftweb.http._
import java.text.ParseException

object DateUtil {
	def parse(str:String) = { 
	  try {
	    new SimpleDateFormat(S ?? "date.format").parse(str)
	  } catch {
	  	case e:ParseException => null 
	  }
	}
}