package code.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import Helpers._
import util._
import Helpers._
import net.liftweb.http.js.JsCmds._
import _root_.scala.xml.Text
import scala.xml.NodeSeq
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.http.{S, SessionVar, SHtml}
import code.model.TrainingSession
import net.liftweb.http.js.JsCmd
import code.model.Training
import net.liftweb.http.RequestVar
import code.comet.DataCenter
import scala.xml.Group
import java.text.SimpleDateFormat
import code.util.DateUtil


class EditTrainingSession {
  
  private object selectedTrainingSession extends RequestVar[Box[TrainingSession]](Empty)
  val format = new SimpleDateFormat(S ?? "datetime.format")
  val timeformat = new SimpleDateFormat(S ?? "time.format")
  
  def listTrainings = {
    val afterDate = new Date(0); // since beginning of time
    ".trainingsession *" #>  TrainingSession.getWithParticipantCount(afterDate).map(t => 
      ".time" #> DateUtil.formatInterval(t.date, t.endDate) &
      ".name" #> t.name &
      ".participantCount" #> t.participantCount &
      ".maxParticipants" #> t.maxParticipants &
      ".place" #> t.place &
      ".remove" #> SHtml.link("confirm", () => loadTrainingSession(t.id), Text(S ?? "Remove")) &
      ".edit" #> SHtml.link("edit_training", () => loadTrainingSession(t.id), Text(S ?? "Edit"))
    ) 
  }
  
  private def loadTrainingSession(id: Long) {
    selectedTrainingSession(TrainingSession.findByKey(id))
  }

  private def save(training: TrainingSession) = training.validate match {
    case Nil => DataCenter.saveTrainingSession(training); S.redirectTo("index.html")
    case x => S.error(x); selectedTrainingSession(Full(training))
  }
  
  def edit(xhtml: NodeSeq): NodeSeq =
    selectedTrainingSession.map(t => 
      <table>
        {t.toForm(Empty, save _)}
        <tr>
          <td><a href="index.html">{S ?? "Cancel"}</a></td>
          <td><input type="submit" value={S ?? "Finish"}/></td>
        </tr>
      </table>
  ) openOr {error(S ?? "training.not-found"); S.redirectTo("index.html")}
  
  def confirmDelete = {
    (for (trainingSession <- selectedTrainingSession.is)
     yield {
        def deleteTraining() {
          DataCenter.removeTrainingSession(trainingSession)
          S.redirectTo("index")
        }

        ".trainingname" #> trainingSession.training.obj.get.name &
        ".date" #> format.format(trainingSession.date.is) &
        ".remove" #> SHtml.submit(S ?? "Remove", deleteTraining _)
    }) 
    match {
      case Full(cssbindfunc) => cssbindfunc
      case _ => error(S ?? "training.not-found"); S.redirectTo("index.html")
    }
  }

}



