package code.model

import _root_.net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.http._
import java.util.Locale

class TrainingSession extends LongKeyedMapper[TrainingSession] with IdPK with OneToMany[Long, TrainingSession] {
  def getSingleton = TrainingSession
  
  object training extends MappedLongForeignKey(this, Training)
  object place extends MappedString(this,100)
  object maxParticipants extends MappedInt(this) {
    
    override def validations =  validateGiven _ :: Nil
    
	def validateGiven(mp : Int) = {
	  if (mp <= 0) {
	    List(FieldError(this, S ?? "training-session.error.max-participants-too-small"))
	  } else {
	    List[FieldError]()
	  }
	}
  }
  object date extends MappedDate(this)
  object participants extends MappedOneToMany(Participant, Participant.trainingSession, OrderBy(Participant.id, Ascending))
  
  MapperRules.displayNameCalculator.default.set({(m : BaseMapper, l : Locale, s : String) => S ?? ("training-session." + s)}) 

  def getWithParticipantCount = 
    DB.runQuery("""select d.id, t.name, count(p.TrainingSession)
                   from TrainingSession d left outer join Participant p on d.id = p.TrainingSession join Training t on d.Training = t.id 
                   group by d.id, t.name""")
                        ._2 // first contains column names
                        .map(list => new TrainingSessionParticipantCountDto(
                                            list(0).toLong,
                                            list(1),
                                            false,
                                            list(2).toLong));

  def getWithParticipantCountForParticipantId(participantName: String) = 
    DB.runQuery("""select depid, depname, has_participated, count(*) from ( 
                     select d.id depid, t.name depname, (select 1 from Participant p2 where p2.name = ? and d.id = p2.TrainingSession) has_participated
                     from TrainingSession d left outer join Participant p on d.id = p.TrainingSession join Training t on d.Training = t.id
                   )
                     group by depid, depname, has_participated""", List(participantName))
                        ._2 // first contains column names
                        .map(list => new TrainingSessionParticipantCountDto(
                                            list(0).toLong,
                                            list(1),
                                            (if (list(2) == "0") false else true),
                                            list(3).toLong));

  
}

object TrainingSession extends TrainingSession with LongKeyedMetaMapper[TrainingSession] {
  override def fieldOrder = List(training,place,date,maxParticipants)
}