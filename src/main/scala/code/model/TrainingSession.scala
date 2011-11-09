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

  MapperRules.displayNameCalculator.default.set({(m : BaseMapper, l : Locale, s : String) => S ?? ("training-session." + s)}
  
) 

 
  def getWithParticipantCount = 
    DB.runQuery("""select d.id, d.name, count(p.Training)
                   from Training d left outer join Participant p on d.id = p.Training
                   group by d.id, d.name""")
                        ._2 // first contains column names
                        .map(list => new TrainingSessionParticipantCountDto(
                                            list(0).toLong,
                                            list(1),
                                            false,
                                            list(2).toLong));

}

object TrainingSession extends TrainingSession with LongKeyedMetaMapper[TrainingSession] {
  override def fieldOrder = List(training,place,date,maxParticipants)
}