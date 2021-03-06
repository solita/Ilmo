package code.model

import _root_.net.liftweb.mapper._
import net.liftweb.http._

class Training extends LongKeyedMapper[Training] with IdPK with OneToMany[Long, Training] {
  def getSingleton = Training

  object name extends MappedString(this, 100) {
    override def validations = { List(valMinLen(1, S ?? "training.error.name-missing"),
                                      valMinLen(5, S ?? "training.error.name-too-short")) }
  }

  object organizer extends MappedString(this, 100) {
    override def validations = List(valMinLen(1, S ?? "training.error.organizer-missing"))
  }
  
  object organizerEmail extends MappedString(this, 100) {
    override def validations = List(valMinLen(1, S ?? "training.error.organizer.email-missing"))
  }

  object description extends MappedTextarea(this, 1500) {
    override def validations = List(valMinLen(1, S ?? "training.error.description-missing"))
  }

  // FIXME: tyypin pitäsi olla mappednullablestring ja paluuarvona box[String]
  object linkToMaterial extends MappedString(this, 100)
  
  object ended extends MappedBoolean(this)
}

object Training extends Training with LongKeyedMetaMapper[Training] {
  override def fieldOrder = List(name, organizer, organizerEmail, description, linkToMaterial)
}