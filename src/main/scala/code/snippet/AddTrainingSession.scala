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


object AddTrainingSession extends LiftScreen {
  
  object trainingSession extends ScreenVar(TrainingSession.create)
   
  override def screenTop = <b>{S ?? "training-session.add"}</b>
  
  val trainings: List[Box[Training]] = Empty :: Training.findAll().map(t => Full(t))
  
  // Fields
  
  val training = select[Box[Training]](S ?? "training-session.training", Empty, trainings)((box: Box[Training]) => box match {
    case Full(t) => t.name.get
    case default => ""
  })

  addFields(() => trainingSession.is.place)
  
  val dateField = text(S ?? "training-session.date", "", FormParam("class", "datepicker"),
	{ s: String => DateUtil.parse(s) match {
	  case null => FieldError(currentField.box.get, Text(S ?? "training-session.error.training-date-format")) :: Nil
	  case d if(d.before(new Date)) => FieldError(currentField.box.get, Text(S ?? "training-session.error.training-date-too-early")) :: Nil
	  case _ => Nil
	}})
	
  addFields(() => trainingSession.is.maxParticipants)
  
  // Validations
  
  override def validations = validateTrainingSession _ :: super.validations
  
  def validateTrainingSession: Errors = {
    var errors: List[FieldError] = Nil 
    if(training.isEmpty) errors = FieldError(training, Text(S ?? "training-session.error.training-missing")) :: errors
    errors
  }
    
  def finish() {
    trainingSession.is.date(DateUtil.parse(dateField.is))
    trainingSession.is.training(training.get)
    trainingSession.is.save
    S.notice(S ?? "training-session.created")
  }
}