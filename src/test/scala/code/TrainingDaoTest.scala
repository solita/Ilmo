package code
import org.specs.SpecificationWithJUnit
import code.model.Training
import net.liftweb.common.Empty
import model.Participant
import net.liftweb.mapper.By
import code.model.TrainingParticipantCountDto2

class TrainingDaoTest extends SpecificationWithJUnit { 
  
  "Trainings" should {  
    "be stored in the database" in {  
      InMemoryDB.init
      var training = Training.create.name("Test").description("desc").saveMe
      var participants = Participant.find(By(Participant.training, training))
      assert(training.name.is == "Test")
      participants mustEqual Empty      
    }
  }
  
  "Trainings" should {  
    "contain participants" in {  
      InMemoryDB.init
      var training = Training.create.name("Test").description("desc").saveMe
      var participant = Participant.create.name("name").training(training).saveMe()
      var participants = Participant.find(By(Participant.training, training))
      
      participants must have size(1)      
    }
  }
  
  "List of Trainings" should {  
    "contain a participant if participant has participated" in {  
      InMemoryDB.init
      
      Training.create.name("training_without_participant").description("desc").save
      
      var training = Training.create.name("training_with_participant").description("desc").saveMe
      
      val participantName = "name"
      Participant.create.name(participantName).training(training).save     
      var trainingList = Training.getWithParticipantCountForParticipantId(participantName)
      
      trainingList must have size(2)
      
      val resultTrainingWithoutParticipants = (trainingList filter (t => t.name == "training_without_participant")).head       
      resultTrainingWithoutParticipants.hasSignedInUserParticipated must beFalse
      
      val resultTrainingWithOneParticipant = (trainingList filter (t => t.name == "training_with_participant")).head           
      resultTrainingWithOneParticipant.hasSignedInUserParticipated must beTrue
      
    }
  }
  
}