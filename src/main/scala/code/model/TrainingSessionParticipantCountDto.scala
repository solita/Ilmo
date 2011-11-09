package code.model
import java.util.Date

class TrainingSessionParticipantCountDto(id: Long, name: String, date: Date, place: String, hasSignedInUserParticipated: Boolean, participantCount: Long) {
  def id() : Long = id;
  def name(): String = name;
  def date(): Date = date;
  def place(): String = place;
  def participantCount(): Long = participantCount;
  def hasSignedInUserParticipated(): Boolean = hasSignedInUserParticipated;
}