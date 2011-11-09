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
}

object Training extends Training with LongKeyedMetaMapper[Training] {
  override def fieldOrder = List(name, organizer, description, linkToMaterial, other)
}