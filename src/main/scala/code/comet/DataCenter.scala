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

abstract class StateChanged
case object TrainingsChanged extends StateChanged
case class TrainingSelected(trainingSessionId: Long) extends StateChanged
case class NewParticipant(name: String, trainingId: Long) extends StateChanged
case class DelParticipant(name: String, trainingId: Long) extends StateChanged
case class UserSignedIn(name: String) extends StateChanged
case object Init extends StateChanged


// FIXME: muuta nimi esim. IlmoListenerManager
object DataCenter extends LiftActor with ListenerManager {
    
    private var message: Option[StateChanged] = Some(Init)
  
    object selectedTrainingSession extends SessionVar[Box[Long]](Empty)
    def getSelectedTrainingSession = selectedTrainingSession.is
    def setSelectedTrainingSession(trainingSessionId: Long) = {
      selectedTrainingSession.set(Full(trainingSessionId))
      notifyListenersWith(TrainingSelected(trainingSessionId))
    }
    
    object currentUserName extends SessionVar[String]("")
    def hasCurrentUserName() = !("" == currentUserName.is)
    def getCurrentUserName() = currentUserName.is
    def clearUserName = setCurrentUserName("")
    def setCurrentUserName(name: String) = {
        currentUserName.set(name)
        notifyListenersWith(UserSignedIn(name))
    }
    
    def notifyListenersWith(messageToListeners: StateChanged) {
      message = Some(messageToListeners)
      updateListeners
    }
    
    def createUpdate = message get
  
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
      notifyListenersWith(NewParticipant(name, trainingSessionId))
    }

    private def delParticipant(name: String, trainingSessionId: Long) = {
      for {
        trainingSession <- TrainingSession.findByKey(trainingSessionId)
        participant <- Participant.findAll(
            By(Participant.trainingSession, trainingSession), 
            By(Participant.name, name)).headOption
      } 
      yield participant.delete_! 
      notifyListenersWith(DelParticipant(name, trainingSessionId))
    }

    def saveAndUpdateListeners[B <: Mapper[B]](entity: B) = {
      entity.save
      notifyListenersWith(TrainingsChanged)
    }

    def removeAndUpdateListeners[B <: Mapper[B]](entity: B) = {
      entity.delete_!
      notifyListenersWith(TrainingsChanged)
    }
    
}
