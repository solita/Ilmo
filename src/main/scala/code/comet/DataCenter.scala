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
import net.liftweb.mapper.Mapper

case class NewParticipant(name: String, trainingId: Long)
case class DelParticipant(name: String, trainingId: Long)
case object StateChanged
    
// FIXME: muuta nimi esim. IlmoApplicationModel
object DataCenter extends LiftActor with ListenerManager {
 
    object selectedTrainingSession extends SessionVar[Box[Long]](Empty)
    def getSelectedTrainingSession = selectedTrainingSession.is
    def setSelectedTrainingSession(trainingSessionId: Long) = {
      selectedTrainingSession.set(Full(trainingSessionId))
      updateListeners
    }
    
    object currentUserName extends SessionVar[String]("")
    def hasCurrentUserName() = !("" == currentUserName.is)
    def getCurrentUserName() = currentUserName.is
    def setCurrentUserName(name: String) = {
        currentUserName.set(name)
        updateListeners
    }
    
    /**
     * When we update the listeners, what message do we send?
     */
    def createUpdate = StateChanged
  
    override def lowPriority = {
      case NewParticipant(name: String, trainingSessionId: Long) =>
        addParticipant(name, trainingSessionId)
      case DelParticipant(name: String, trainingSessionId: Long) =>
        delParticipant(name, trainingSessionId)      
    }
    
    private def addParticipant(name: String, trainingSessionId: Long) = {
      TrainingSession.findByKey(trainingSessionId) match {
        case Full(trainingSession) =>
          Participant.create.name(name).trainingSession(trainingSession).save
        case _ => // this could happen if training was removed, in this app we dont care..
      }
      updateListeners
    }

    private def delParticipant(name: String, trainingSessionId: Long) = {
      for {
        trainingSession <- TrainingSession.findByKey(trainingSessionId)
        participant <- Participant.findAll(
            By(Participant.trainingSession, trainingSession), 
            By(Participant.name, name)).headOption
      } 
      yield participant.delete_! 
      updateListeners
    }

    def saveAndUpdateListeners[B <: Mapper[B]](entity: B) = {
      entity.save
      updateListeners
    }

    def removeAndUpdateListeners[B <: Mapper[B]](entity: B) = {
      entity.delete_!
      updateListeners
    }
    
}
