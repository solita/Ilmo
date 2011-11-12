package code.snippet

import net.liftweb._
import http._
import code.model.Training
import code.comet.DataCenter
import code.comet.NewTraining

object AddTraining extends LiftScreen {
  
  object training extends ScreenVar(Training.create)
  
  override def screenTop = <b>{S ?? "training.add"}</b>

  addFields(() => training.is)
    
  def finish() {
    training.is.save
    DataCenter ! NewTraining(training.is.name)
    S.notice(training.is.name + " " + S ?? "training.created")
  }
}