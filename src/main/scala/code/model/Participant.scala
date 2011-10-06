package code.model

import _root_.net.liftweb.mapper._
import net.liftweb.common.{Box, Full}

class Participant extends LongKeyedMapper[Participant] with IdPK {
  def getSingleton = Participant

  object name extends MappedString(this, 50) {
    override def validations = { List(valMinLen(2, "nimi on pakollinen tieto"),
                                      valMaxLen(50, "liian pitkä nimi")) }
  }

  object training extends MappedLongForeignKey(this, Training) {
    override def validSelectValues: Box[List[(Long, String)]] =
        Full(Training.findAll.map(d => (d.id.is, d.name.is)))
  }

}

object Participant extends Participant with LongKeyedMetaMapper[Participant] {
  override def fieldOrder = List(name, training)
}