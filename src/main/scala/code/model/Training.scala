package code.model

import _root_.net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.http._
import java.util.Locale

class Training extends LongKeyedMapper[Training] with IdPK with OneToMany[Long, Training] {
  def getSingleton = Training

  object name extends MappedString(this, 100) {
    override def validations = { List(valMinLen(1, S ?? "training.error.name-missing"),
                                      valMinLen(5, S ?? "training.error.name-too-short")) }
  }

  object organizer extends MappedString(this, 100) {
    override def validations = List(valMinLen(1, S ?? "training.error.organizer-missing"))
  }
  
  object linkToMaterial extends MappedString(this,100)
  object description extends MappedTextarea(this, 1500)
  object other extends MappedTextarea(this, 1500) 
}

object Training extends Training with LongKeyedMetaMapper[Training] {
  override def fieldOrder = List(name, organizer, description, linkToMaterial, other)
}