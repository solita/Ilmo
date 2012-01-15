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
        // todo loputtomiin kasvava lista on huono idea     
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
  def getWithParticipantCount(afterDate: Date) = 
    DB.runQuery("""select ts.id, t.name, ts.date_c, ts.endDate, ts.place, count(p.TrainingSession), ts.maxParticipants 
                   from TrainingSession ts left outer join Participant p on ts.id = p.TrainingSession join Training t on ts.Training = t.id
    			   where ts.date_c >= ?
                   group by ts.id, t.name, ts.date_c, ts.endDate, ts.place, ts.maxParticipants
                   order by ts.date_c desc, ts.id""", List(afterDate))
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

  def getWithParticipantCountForParticipantId(participantName: String, afterDate: Date) =     
    DB.runQuery(
        """select sessionid, sessionname, sessiondate, sessionEndDate, place, has_participated, participantCount, maxparts from ( 
               select s.id sessionid, t.name sessionname, s.date_c sessiondate, s.endDate sessionEndDate, s.place place,         
                      (select case when count(*)>0 then 'Y' else 'N' end from Participant p2 where p2.name = ? and s.id = p2.TrainingSession) has_participated,        
                      (select count(*) from Participant p where p.TrainingSession = s.id) participantCount,
                      s.maxParticipants as maxparts
               from TrainingSession s join Training t on s.Training = t.id
               where s.date_c >= ?
           )
           order by sessiondate desc, sessionid""",
        List(participantName, afterDate))._2 
           .map(list => new TrainingSessionParticipantCountDto(
                list(0).toLong,
                list(1),
                DateUtil.parseSqlDate(list(2)),
                DateUtil.parseSqlDate(list(3)),
                list(4),
                (if (list(5) == "Y") true else false),
                list(6).toLong,
                list(7).toLong));

  
    def getMonthlyParticipantCount(afterDate: Date) = {
        DB.runQuery("""
          select y, m, count(*) from (
              select extract(year from date_c) as y, extract(month from date_c) as m
              from trainingsession s inner join participant p on p.trainingsession = s.id
              where date_c >= ?
          )     
          group by y, m
        """, List(afterDate))._2.map(
            list => MonthlyParticipantCountDto(list(0).toInt, list(1).toInt, list(2).toInt))
    }

}