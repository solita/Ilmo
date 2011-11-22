package code.util
import net.liftweb.util.DateTimeConverter
import java.util.Date
import net.liftweb.util.DefaultDateTimeConverter
import net.liftweb.util.Helpers
import net.liftweb.http.S

class IlmoDateFormatter extends DateTimeConverter {
  def formatDateTime(d: Date) = DateUtil.formatDateTime(d)
  def formatDate(d: Date) = DateUtil.format(d)
  def formatTime(d: Date) = DateUtil.formatTime(d)

  def parseDateTime(s: String) = Helpers.tryo { DateUtil.parseDateTime(s) }
  def parseDate(s: String) = Helpers.tryo { DateUtil.parseDate(s) }
  def parseTime(s: String) = Helpers.tryo { DateUtil.parseTime(s) }
}

