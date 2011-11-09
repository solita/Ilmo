package code.model

import _root_.net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.http._
import java.util.Locale

class Training extends LongKeyedMapper[Training] with IdPK with OneToMany[Long, Training] {
  def getSingleton = Training

  object name extends MappedString(this, 100) {
    
    override def validations =  validateGiven _ :: Nil

    def validateGiven(name : String) = {
      if (organizer.length == 0) {
        List(FieldError(this, S ?? "training.error.name-missing"))
      } else if (name.length < 5) {
        List(FieldError(this, S ?? "training.error.name-too-short"))
      } else {
        List[FieldError]()
      }
    }
    
  }

  object organizer extends MappedString(this, 100) {
    
    override def validations =  validateGiven _ :: Nil
    
	def validateGiven(organizer : String) = {
	  if (organizer.length == 0) {
	    List(FieldError(this, S ?? "training.error.organizer-missing"))
	  } else {
	    List[FieldError]()
	  }
	}
    
  }
  
  object linkToMaterial extends MappedString(this,100)
  object description extends MappedTextarea(this, 1500)
  object other extends MappedTextarea(this, 1500)

  object participants extends MappedOneToMany(Participant, Participant.training, 
      OrderBy(Participant.id, Ascending))
  
  MapperRules.displayNameCalculator.default.set({(m : BaseMapper, l : Locale, s : String) => S ?? ("training." + s)}
  
) 

 
  def getWithParticipantCount = 
    DB.runQuery("""select d.id, d.name, count(p.Training)
                   from Training d left outer join Participant p on d.id = p.Training
                   group by d.id, d.name""")
                        ._2 // first contains column names
                        .map(list => new TrainingParticipantCountDto(
                                            list(0).toLong,
                                            list(1),
                                            list(2).toLong));

  def getWithParticipantCountForParticipantId(participantName: String) = 
    DB.runQuery("""select depid, depname, has_participated, count(*) from ( 
                     select d.id depid, d.name depname, (select 1 from Participant p2 where p2.name = ? and d.id = p2.Training) has_participated
                     from Training d left outer join Participant p on d.id = p.Training
                   )
                     group by depid, depname, has_participated""", List(participantName))
                        ._2 // first contains column names
                        .map(list => new TrainingParticipantCountDto2(
                                            list(0).toLong,
                                            list(1),
                                            (if (list(2) == "0") false else true),
                                            list(3).toLong));
  
}

object Training extends Training with LongKeyedMetaMapper[Training] {
  override def fieldOrder = List(name, organizer, description, linkToMaterial, other)
}