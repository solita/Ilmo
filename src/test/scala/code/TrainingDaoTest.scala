package code
import org.specs.SpecificationWithJUnit
import code.model.Training
import net.liftweb.common.Empty
import model.Participant
import net.liftweb.mapper.By

class TrainingDaoTest extends SpecificationWithJUnit { 
  
  "Trainings" should {  
    "be stored in the database" in {  
      InMemoryDB.init
      var training = Training.create.name("Test").description("desc").saveMe
      var participants = Participant.find(By(Participant.training, training))
      assert(training.name.is == "Test")
      participants must_==Empty      
    }
  }
  
  "Trainings" should {  
    "contain participants" in {  
      InMemoryDB.init
      var training = Training.create.name("Test").description("desc").saveMe
      var participant = Participant.create.name("name").training(training).saveMe()
      var participants = Participant.find(By(Participant.training, training))
      
      participants.size must_==1      
    }
  }
  
  "List of Trainings" should {  
    "contain a participant if participant has participated" in {  
      InMemoryDB.init
      
      Training.create.name("dummy").description("desc").save
      
      var training = Training.create.name("Test").description("desc").saveMe
      Participant.create.name("name").training(training).save
      
      var trainingList = Training.getWithParticipantCountForParticipantId("name")
      
      trainingList.size must_==2
      // TODO tutki listaa
    }
  }
  
}