package code.model

class TrainingSessionParticipantCountDto(id: Long, name: String, hasSignedInUserParticipated: Boolean, participantCount: Long) {
  def id() : Long = id;
  def name(): String = name;
  def participantCount(): Long = participantCount;
  def hasSignedInUserParticipated(): Boolean = hasSignedInUserParticipated;
}