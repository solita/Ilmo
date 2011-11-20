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
import code.model.TrainingSession
import code.model.Participant
import net.liftweb.mapper.By

case class NewTraining(name: String)
case class NewTrainingSession
case class NewParticipant(name: String, trainingId: Long)
case class DelParticipant(name: String, trainingId: Long)
case class TrainingDeleted
case class RegisterMsg(name : String)
case class SignIn
    
object DataCenter extends LiftActor with ListenerManager {
 
    object selectedTrainingSession extends SessionVar[Box[Long]](Empty)
    def getSelectedTrainingSession = selectedTrainingSession.is
    def setSelectedTrainingSession(trainingSessionId: Long) = {
      selectedTrainingSession.set(Full(trainingSessionId))
      updateListeners
    }
    
    object signInName extends SessionVar[String]("")
    
    def hasSignInName() = !("" == signInName.is)
    
    def getName() = signInName.is
    
    def setName(name: String) = {
        signInName.set(name)
        updateListeners
    }
    
    /**
     * When we update the listeners, what message do we send?
     */
    def createUpdate = SignIn
  
    override def lowPriority = {
      case RegisterMsg(name: String) => {
        println("got register msg")
        setName(name)
        updateListeners()
      }
      case NewParticipant(name: String, trainingSessionId: Long) =>
        addParticipant(name, trainingSessionId)
      case DelParticipant(name: String, trainingSessionId: Long) =>
        delParticipant(name, trainingSessionId)      
    }
    
    def addParticipant(name: String, trainingSessionId: Long) = {
      println("saving " + name + " to " + trainingSessionId)
      TrainingSession.findByKey(trainingSessionId) match {
        case Full(trainingSession) => {
          Participant.create.name(name).trainingSession(trainingSession).save
        }
      }
      updateListeners
    }

    def delParticipant(name: String, trainingSessionId: Long) = {
      for {
        trainingSession <- TrainingSession.findByKey(trainingSessionId)
        participant <- Participant.findAll(
            By(Participant.trainingSession, trainingSession), 
            By(Participant.name, name)).headOption
      } 
      yield participant.delete_! 
      updateListeners
    }

    def addTrainingSession(trainingSession: TrainingSession) =  {
      trainingSession.save 
      updateListeners
    }
    
    def removeTraining(training: Training) = {
      training.delete_!
      updateListeners
    }
    
    
}
