package code.snippet

import net.liftweb._
import http._
import code.model.Training
import code.comet.DataCenter

object AddTraining extends LiftScreen {
  
  object training extends ScreenVar(Training.create)
  
  override def screenTop = <b>{S ?? "training.add"}</b>

  addFields(() => training.is)
    
  def finish() {
    DataCenter.saveAndUpdateListeners(training.is)
    S.notice(training.is.name + " " + S ?? "training.created")
  }
}