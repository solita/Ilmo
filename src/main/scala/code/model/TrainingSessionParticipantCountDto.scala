package code.model
import java.util.Date

case class TrainingSessionParticipantCountDto(
  id:      Long,
  name:    String,
  date:    Date,
  endDate: Date,
  place:   String,
  hasSignedInUserParticipated: Boolean,
  participantCount: Long,
  maxParticipants: Long
)