package code.snippet

import net.liftweb._
import http._
import code.model.Training

object AddTraining extends LiftScreen {
  
  object training extends ScreenVar(Training.create)
  
  override def screenTop = <b>{S ? "Add training"}</b>

  addFields(() => training.is)
    
  def finish() {
    training.is.save
    S.notice(training.is.name + " luotu")
  }
}