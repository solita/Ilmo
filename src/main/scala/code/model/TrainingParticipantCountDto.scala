package code.model

class TrainingParticipantCountDto(id: Long, name: String, participantCount: Long) {
  def id() : Long = id;
  def name(): String = name;
  def participantCount(): Long = participantCount;
}