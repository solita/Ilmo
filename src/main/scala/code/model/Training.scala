package code.model

import _root_.net.liftweb.mapper._

class Training extends LongKeyedMapper[Training] with IdPK with OneToMany[Long, Training] {
  def getSingleton = Training

  object name extends MappedString(this, 100)

  object participants extends MappedOneToMany(Participant, Participant.training, 
      OrderBy(Participant.id, Ascending))
 
  def getWithParticipantCount = 
    DB.runQuery("""select d.id, d.name, count(p.Training)
                   from Training d left outer join Participant p on d.id = p.Training
                   group by d.id, d.name""")
                        ._2 // first contains column names
                        .map(list => new TrainingParticipantCountDto(
                                            list(0).toLong,
                                            list(1),
                                            list(2).toLong));

}

object Training extends Training with LongKeyedMetaMapper[Training] {
  override def fieldOrder = List(name)
}