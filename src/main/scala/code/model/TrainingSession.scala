package code.model

import _root_.net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.http._
import java.util.Locale
import java.util.Date
import code.util.DateUtil
import net.liftweb.common.Full
import net.liftweb.common.Box

class TrainingSession extends LongKeyedMapper[TrainingSession] with IdPK with OneToMany[Long, TrainingSession] {
  def getSingleton = TrainingSession
  
  object training extends MappedLongForeignKey(this, Training) {
    override def validSelectValues: Box[List[(Long, String)]] =
        Full(Training.findAll.map(d => (d.id.is, d.name.is)))
  }
  
  object place extends MappedString(this,100)
  object maxParticipants extends MappedInt(this) {
    
    override def validations =  validateGiven _ :: Nil
    
	def validateGiven(mp : Int) = {
	  if (mp <= 0) {
	    List(FieldError(this, S ?? "trainingsession.error.max-participants-too-small"))
	  } else {
	    List[FieldError]()
	  }
	}
  }
  object date extends MappedDateTime(this)
  object endDate extends MappedDateTime(this)
  object participants extends MappedOneToMany(Participant, Participant.trainingSession, OrderBy(Participant.id, Ascending)) 
}

object TrainingSession extends TrainingSession with LongKeyedMetaMapper[TrainingSession] {
  override def fieldOrder = List(training,place,date,endDate,maxParticipants)
  
  //TODO: Saisiko näitä kahta metodia refaktoroitua jotenkin siistimmäksi?
  def getWithParticipantCount = 
    DB.runQuery("""select d.id, t.name, d.date_c, d.endDate, d.place, count(p.TrainingSession), d.maxParticipants 
                   from TrainingSession d left outer join Participant p on d.id = p.TrainingSession join Training t on d.Training = t.id 
                   group by d.id, t.name order by d.date_c, d.id""")
                        ._2 // first contains column names
                        .map(list => new TrainingSessionParticipantCountDto(
                                            list(0).toLong,
                                            list(1),
                                            DateUtil.parseSqlDate(list(2)),
                                            DateUtil.parseSqlDate(list(3)),
                                            list(4),
                                            false,
                                            list(5).toLong,
                                            list(6).toLong));

  def getWithParticipantCountForParticipantId(participantName: String) = 
    DB.runQuery("""select sessionid, sessionname, sessiondate, sessionEndDate, place, has_participated, participants, maxparts from ( 
                     select s.id sessionid, t.name sessionname, s.date_c sessiondate, s.endDate sessionEndDate, s.place place, 
    				 	(select 1 from Participant p2 where p2.name = ? and s.id = p2.TrainingSession) has_participated,
    					(select count(*) from Participant p where p.TrainingSession = s.id) participants,
    					s.maxParticipants as maxparts
                     from TrainingSession s join Training t on s.Training = t.id
                   ) group by sessionid, sessionname, has_participated order by sessiondate, sessionid""", List(participantName))
                        ._2 // first contains column names
                        .map(list => new TrainingSessionParticipantCountDto(
                                            list(0).toLong,
                                            list(1),
                                            DateUtil.parseSqlDate(list(2)),
                                            DateUtil.parseSqlDate(list(3)),
                                            list(4),
                                            (if (list(5) == "0") false else true),
                                            list(6).toLong,
                                            list(7).toLong));

}