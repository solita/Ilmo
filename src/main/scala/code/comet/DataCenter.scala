package code.comet

import net.liftweb.util._
import net.liftweb._
import http._
import actor._
import scala.actors._
import scala.actors.Actor._
import scala.util.Random
import net.liftweb.common.Box
import net.liftweb.common.Empty
import net.liftweb.common.Full
import code.model.Training
import code.model.Participant

case class NewTraining(name: String)
case class NewParticipant(name: String, trainingId: Long)
case class TrainingDeleted
case class RegisterMsg(name : String)
case class SignIn
    
object DataCenter extends LiftActor with ListenerManager {
 
    object selectedTraining extends SessionVar[Box[Long]](Empty)
    def getSelectedTraining = selectedTraining.is
    def setSelectedTraining(trainingId: Long) = {
      selectedTraining.set(Full(trainingId))
      updateListeners
    }
    
    object firstname extends SessionVar[String]("")
    
    def hasSignInName() = !("" == firstname.is)
    
    def getName() = firstname.is
    
    def setName(name: String) = {
        firstname.set(name)
        updateListeners
    }
    
    /**
     * When we update the listeners, what message do we send?
     * We send the msgs, which is an immutable data structure,
     * so it can be shared with lots of threads without any
     * danger or locking.
     */
    def createUpdate = SignIn
  
    override def lowPriority = {
      case RegisterMsg(name: String) => {
        println("got register msg")
        setName(name)
        updateListeners()
      }
      case NewParticipant(name: String, trainingId: Long) => {
        addParticipant(name, trainingId)
      }
    }
    
    def addParticipant(name: String, trainingId: Long) {
      println("saving " + name + " to " + trainingId)
      Training.findByKey(trainingId) match {
        case Full(training) => {
          Participant.create.name(name).training(training).save
      }
        updateListeners
    }
  }
    
}
