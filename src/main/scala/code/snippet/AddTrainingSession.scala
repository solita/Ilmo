package code.snippet

import net.liftweb._
import http._
import code.model.{Training, TrainingSession}

object AddTrainingSession extends LiftScreen {
  
  object trainingSession extends ScreenVar(TrainingSession.create)
  
  override def screenTop = <b>{S ?? "training-session.add"}</b>

  addFields(() => trainingSession.is)
    
  def finish() {
    trainingSession.is.save
    S.notice(S ?? "training-session.created")
  }
}