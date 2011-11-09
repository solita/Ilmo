package code
import org.specs.SpecificationWithJUnit
import code.model.Training
import net.liftweb.common.Empty
import model.Participant
import net.liftweb.mapper.By
import code.model.TrainingSessionParticipantCountDto
import code.model.TrainingSession
import java.util.Date

class TrainingSessionDaoTest extends SpecificationWithJUnit { 
  
  "Trainings" should {  
    "be stored in the database" in {  
      InMemoryDB.init
      var training = Training.create.name("Test").description("desc").saveMe
      var trainingSession = TrainingSession.create.date(new Date).training(training).place("Place").saveMe
      var participants = Participant.find(By(Participant.trainingSession, trainingSession))
      assert(training.name.is == "Test")
      participants mustEqual Empty      
    }
  }
  
  "Trainings" should {  
    "contain participants" in {  
      InMemoryDB.init
      var training = Training.create.name("Test").description("desc").saveMe
      var trainingSession = TrainingSession.create.date(new Date).training(training).place("Place").saveMe
      var participant = Participant.create.name("name").trainingSession(trainingSession).saveMe()
      var participants = Participant.find(By(Participant.trainingSession, trainingSession))
      participants must have size(1)      
    }
  }
  
  "List of Trainings" should {  
    "contain a participant if participant has participated" in {  
      InMemoryDB.init
      
      var trainingNoParticipants = Training.create.name("training_without_participant").description("desc").saveMe
      TrainingSession.create.date(new Date).training(trainingNoParticipants).place("Place").saveMe
      
      var training = Training.create.name("training_with_participant").description("desc").saveMe
      var trainingSession = TrainingSession.create.date(new Date).training(training).place("Place").saveMe
      
      val participantName = "name"
      Participant.create.name(participantName).trainingSession(trainingSession).save     
      var trainingList = TrainingSession.getWithParticipantCountForParticipantId(participantName)
      
      trainingList must have size(2)
      
      val resultTrainingWithoutParticipants = (trainingList filter (t => t.name == "training_without_participant")).head       
      resultTrainingWithoutParticipants.hasSignedInUserParticipated must beFalse
      
      val resultTrainingWithOneParticipant = (trainingList filter (t => t.name == "training_with_participant")).head           
      resultTrainingWithOneParticipant.hasSignedInUserParticipated must beTrue
      
    }
  }
  
}