package code.snippet

import net.liftweb._
import http._
import code.model.{Training, TrainingSession}
import net.liftweb.common._
import net.liftweb.util.FieldError
import scala.xml.Text
import java.util.Date

object AddTrainingSession extends LiftScreen {
  
  object trainingSession extends ScreenVar(TrainingSession.create)
   
  override def screenTop = <b>{S ?? "training-session.add"}</b>
  
  val trainings: List[Box[Training]] = Empty :: Training.findAll().map(t => Full(t))
  
  var training = select[Box[Training]](S ?? "training-session.training", Empty, trainings)((box: Box[Training]) => box match {
    case Full(t) => t.name.get
    case default => ""
  })

  addFields(() => trainingSession.is.place)
  addFields(() => trainingSession.is.date)
  addFields(() => trainingSession.is.maxParticipants)
  
  override def validations = validateTrainingSession _ :: super.validations
  
  def validateTrainingSession: Errors = {
    var errors: List[FieldError] = Nil 
    if(training.isEmpty) errors = FieldError(training, Text(S ?? "training-session.error.training-missing")) :: errors
    if(trainingSession.is.date.get == null) errors = FieldError(trainingSession.is.date, Text(S ?? "training-session.error.training-date-missing")) :: errors
    else if(trainingSession.is.date.get.before(new Date)) errors = FieldError(trainingSession.is.date, Text(S ?? "training-session.error.training-date-too-early")) :: errors
    errors
  }
    
  def finish() {
    trainingSession.is.training(training.get)
    trainingSession.is.save
    S.notice(S ?? "training-session.created")
  }
}