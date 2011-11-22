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
      var trainingList = TrainingSession.getWithParticipantCountForParticipantId(participantName, new Date(0))
      
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

      var trainingList = TrainingSession.getWithParticipantCount(new Date(0))
      
      trainingList must have size(2)
      
      val resultTrainingWithParticipants = (trainingList filter (t => t.name == "training_with_participant")).head           
      resultTrainingWithParticipants.participantCount must be equalTo(2)
      
    }
  }
  
  "List of Trainings" should {
    "should contain only trainings after the given date" in {
      InMemoryDB.init
      val cutofTime = new Date(100,4,5)
      var training = Training.create.name("old_training").description("desc").saveMe
      TrainingSession.create.date(new Date(100,0,5)).endDate(new Date).training(training).place("Place").saveMe
      TrainingSession.create.date(cutofTime).endDate(new Date).training(training).place("Place").saveMe
      TrainingSession.create.date(new Date(101,0,5)).endDate(new Date).training(training).place("Place").saveMe
      
      var trainingList = TrainingSession.getWithParticipantCount(cutofTime)
      trainingList must have size(2)
      trainingList foreach(t => {
        t.date().before(cutofTime) must beFalse
      }
      )
    }
  }
}