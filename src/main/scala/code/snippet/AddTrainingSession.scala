package code.snippet

import net.liftweb._
import http._
import code.model.{Training, TrainingSession}
import code.util._
import net.liftweb.common._
import net.liftweb.util.FieldError
import scala.xml.Text
import java.util.Date
import net.liftweb.util.FieldIdentifier
import java.text.SimpleDateFormat
import java.text.ParseException
import scala.xml.NodeSeq
import org.w3c.dom.Attr
import scala.xml.Attribute
import code.comet.DataCenter


object AddTrainingSession extends LiftScreen {
   
  override def screenTop = <b>{S ?? "trainingsession.add"}</b>
  
  object trainingSession extends ScreenVar(TrainingSession.create)
  
  // Fields
  
  addFields(() => trainingSession.is.training)
  addFields(() => trainingSession.is.place)
  
  val dateField = text(S ?? "trainingsession.date", "", FormParam("class", "datepicker"),
	{ s: String => DateUtil.parse(s) match {
	  case null => FieldError(currentField.box.get, Text(S ?? "trainingsession.error.training-date-format")) :: Nil
	  case d if(d.before(new Date)) => FieldError(currentField.box.get, Text(S ?? "trainingsession.error.training-date-too-early")) :: Nil
	  case _ => Nil
	}})
	
  addFields(() => trainingSession.is.maxParticipants)
  
  def finish() {
    trainingSession.is.date(DateUtil.parse(dateField.is))
    DataCenter.saveTrainingSession(trainingSession.is)
    S.notice(S ?? "trainingsession.created")
  }
}