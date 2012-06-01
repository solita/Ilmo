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
import net.liftweb.mapper.Like

abstract class StateChanged
case object TrainingsChanged extends StateChanged
case class TrainingSelected(trainingSessionId: Long) extends StateChanged
case class NewParticipant(name: String, trainingId: Long) extends StateChanged
case class DelParticipant(name: String, trainingId: Long) extends StateChanged
case class UserSignedIn(name: String) extends StateChanged
case class UserSignedOut(name: String) extends StateChanged
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
    
    object previousUserName extends SessionVar[String]("")
    object currentUserName extends SessionVar[String]("")
    def hasCurrentUserName() = !("" == currentUserName.is)
    def getCurrentUserName() = currentUserName.is
    def isMyUser(username: String) = getCurrentUserName equals username
    def wasMyUser(username: String) = previousUserName.is equals username
    def signout(username: String) = {
        currentUserName.set("")
        previousUserName.set(username)
        notifyListenersWith(UserSignedOut(username))
    }
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
        addParticipantIfNotExists(name, trainingSessionId)
      case DelParticipant(name: String, trainingSessionId: Long) =>
        delParticipant(name, trainingSessionId)      
    }
    
    private def addParticipantIfNotExists(name: String, trainingSessionId: Long) = {
      val existingParticipant = Participant.findAll(
          By(Participant.trainingSession, trainingSessionId), Like(Participant.name, name))

      if ( existingParticipant.isEmpty ) addParticipant(name, trainingSessionId);
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
        training <- Training.findByKey(trainingSession.training)
        participants <- Full(trainingSession.participants)
        participant <- participants.filter(p => p.name.equals(name)).headOption
      } 
      yield {
        participant.delete_!
        if (trainingSession.maxParticipants < participants.length) {
          val newParticipant = participants.drop(trainingSession.maxParticipants).head
          IlmoMailSender.notifyVarasijaltaOsallistujaksi(participant.name, newParticipant.name, training.name, trainingSession.date)
        }
      }
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
    
    def ilmomsg(msg: Any) = msg.isInstanceOf[StateChanged]
}
