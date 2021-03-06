package code.snippet

import net.liftweb.util._
import net.liftweb.common._
import Helpers._
import _root_.scala.xml.Text
import scala.xml.NodeSeq
import net.liftweb.http.{S, SHtml}
import code.model.Training
import net.liftweb.http.RequestVar
import code.comet.DataCenter
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Descending
import code.util.UrlFormatter


class EditTrainings {
  
  private object selectedTraining extends RequestVar[Box[Training]](Empty)
  
  def listTrainings = {
    
    ".training *" #>  Training.findAll(OrderBy(Training.id, Descending)).map(training => 
      ".name" #> training.name &
      ".organizer" #> training.organizer &
      ".linkToMaterial" #> toEmptySpanOrLink(training.linkToMaterial) &
      ".remove" #> SHtml.link("confirm", () => selectedTraining(Full(training)), Text(S ?? "Remove")) &
      ".edit" #> SHtml.link("edit_training", () => selectedTraining(Full(training)), Text(S ?? "Edit"))
    ) 
  }
  
  private def toEmptySpanOrLink(link: String): NodeSeq = {
    if (link == null) {
      Text("")
    } else {
      <a href={UrlFormatter.addUrlProtocolIfNecessary(link)}>{link}</a>
    }
  }

  private def saveTraining(training: Training) = training.validate match {
    case Nil => DataCenter.saveAndUpdateListeners(training); S.redirectTo("index.html")
    case x => S.error(x); selectedTraining(Full(training))
  }
  
  def edit(xhtml: NodeSeq): NodeSeq =
    selectedTraining.map(t => 
      <table>
        {t.toForm(Empty, saveTraining _)}
        <tr>
          <td><a href="index.html">{S ?? "Cancel"}</a></td>
          <td><input type="submit" value={S ?? "Finish"}/></td>
        </tr>
      </table>
  ) openOr {S.error(S ?? "training.not-found"); S.redirectTo("index.html")}
  
  def confirmDelete = {
    (for (training <- selectedTraining.is)
     yield {
        def deleteTraining() {
          DataCenter.removeAndUpdateListeners(training)
          S.redirectTo("index")
        }

        ".trainingname" #> training.name &
        ".remove" #> SHtml.submit(S ?? "Remove", deleteTraining _)
    }) 
    match {
      case Full(cssbindfunc) => cssbindfunc
      case _ => S.error(S ?? "training.not-found"); S.redirectTo("index.html")
    }
  }

}



