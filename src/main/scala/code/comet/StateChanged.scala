package code.comet

sealed trait StateChanged

case object Init extends StateChanged
case object TrainingsChanged extends StateChanged

case class TrainingSelected(trainingSessionId: Long) extends StateChanged
case class NewParticipant(name: String, trainingId: Long) extends StateChanged
case class DelParticipant(name: String, trainingId: Long) extends StateChanged
case class UserSignedIn(name: String) extends StateChanged
case class UserSignedOut(name: String) extends StateChanged