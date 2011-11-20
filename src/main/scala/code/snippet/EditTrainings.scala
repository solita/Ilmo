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


class EditTrainings {
  
  private object selectedTraining extends RequestVar[Box[Training]](Empty)
  
  def listTrainings = {
    
    ".training *" #>  Training.findAll.map(training => 
      ".name" #> training.name &
      ".organizer" #> training.organizer &
      ".linkToMaterial" #> <a href={training.linkToMaterial}>{training.linkToMaterial}</a> &
      ".remove" #> SHtml.link("#", () => DataCenter.removeTraining(training), Text(S ?? "Remove")) &
      ".edit" #> SHtml.link("edit_training", () => selectedTraining(Full(training)), Text(S ?? "Edit"))
    ) 
  }

  private def saveTraining(training: Training) = training.validate match {
    case Nil => training.save; S.redirectTo("/edit_training/index.html")
    case x => S.error(x); selectedTraining(Full(training))
  }
  
  def edit(xhtml: NodeSeq): NodeSeq =
    selectedTraining.map(t => 
      <table>
        {t.toForm(Empty, saveTraining _)}
        <tr>
          <td><a href="/edit_training/index.html">Cancel</a></td>
          <td><input type="submit" value="Save"/></td>
        </tr>
      </table>
  ) openOr {error("Training not found"); S.redirectTo("/edit_training/index.html")}
  
}



