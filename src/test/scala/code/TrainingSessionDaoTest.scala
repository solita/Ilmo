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
  
  "List of Trainings" should {  
    "contain a participant if participant has participated" in {  
      InMemoryDB.init
      
      var trainingNoParticipants = Training.create.name("training_without_participant").description("desc").saveMe
      TrainingSession.create.date(new Date).endDate(new Date).training(trainingNoParticipants).place("Place").saveMe
      
      var training = Training.create.name("training_with_participant").description("desc").saveMe
      var trainingSession = TrainingSession.create.date(new Date).endDate(new Date).training(training).place("Place").saveMe
      
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
  
  
  "List of Trainings" should {  
    "should have correct participant count" in {  
      InMemoryDB.init
      
      var trainingNoParticipants = Training.create.name("training_without_participant").description("desc").saveMe
      TrainingSession.create.date(new Date).endDate(new Date).training(trainingNoParticipants).place("Place").saveMe
      
      var training = Training.create.name("training_with_participant").description("desc").saveMe
      var trainingSession = TrainingSession.create.date(new Date).endDate(new Date).training(training).place("Place").saveMe
      
      Participant.create.name("p1").trainingSession(trainingSession).save
      Participant.create.name("p2").trainingSession(trainingSession).save 

      var trainingList = TrainingSession.getWithParticipantCount
      
      trainingList must have size(2)
      
      val resultTrainingWithParticipants = (trainingList filter (t => t.name == "training_with_participant")).head           
      resultTrainingWithParticipants.participantCount must be equalTo(2)
      
    }
  }
}