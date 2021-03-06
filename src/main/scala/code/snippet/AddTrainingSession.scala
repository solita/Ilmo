package code.snippet

import net.liftweb._
import http._
import code.model.TrainingSession
import code.util._
import net.liftweb.util.FieldError
import scala.xml.Text
import java.util.Date
import code.comet.DataCenter


object AddTrainingSession extends LiftScreen {
   
  override def screenTop = <b>{S ?? "trainingsession.add"}</b>
  
  object trainingSession extends ScreenVar(TrainingSession.create)
  
  // Fields
  
  addFields(() => trainingSession.is.training)
  addFields(() => trainingSession.is.place)
  
  def date(title: String) = text(S ?? title, "", FormParam("class", "datepicker"),
  { s: String => DateUtil.parseDateTime(s) match {
    case null => FieldError(currentField.box.get, Text(S ?? "trainingsession.error.training-date-format")) :: Nil
    case d if(d.before(new Date)) => FieldError(currentField.box.get, Text(S ?? "trainingsession.error.training-date-too-early")) :: Nil
    case _ => Nil
  }})
  
  val dateField = date("trainingsession.date")
  val endDateField = date("trainingsession.endDate")

  addFields(() => trainingSession.is.maxParticipants)
  
  override def validations = validateTrainingSession _ :: super.validations
  
  def validateTrainingSession: Errors = {
    val start = DateUtil.parseDateTime(dateField.is)
    val end   = DateUtil.parseDateTime(endDateField.is)

    if (end.before(start)) {
      FieldError(endDateField, Text(S ?? "trainingsession.error.end-before-start")) :: Nil
    }
    else Nil
  }
  
  def finish() {
    trainingSession.is.date(DateUtil.parseDateTime(dateField.is))
    trainingSession.is.endDate(DateUtil.parseDateTime(endDateField.is))
    DataCenter.saveAndUpdateListeners(trainingSession.is)
    S.notice(S ?? "trainingsession.created")
  }
}