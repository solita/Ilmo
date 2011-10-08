package code.snippet 

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import Helpers._
import code.model._
import js.JsCmds.SetHtml
import js.{JsCmd, JsCmds}

class MainSnippet {

  def viewParticipants(trainingId: Long, participantCount: Long) : JsCmd = {
    if (participantCount == 0)
      SetHtml("participant_table", Text("Ei osallistujia"))
    else
      SetHtml("participant_table", buildParticipantTable(trainingId))
  }

  def buildParticipantTable(trainingId: Long) : NodeSeq = {
    Training.findByKey(trainingId) match {
      case Full(training) => training.participants.flatMap(
          participant => <tr>
                           <td>{participant.name.is}</td>
                         </tr>)

      case _ => Text("You are screwed")
    }
  }
  
  def listTrainings(xhtml : NodeSeq) : NodeSeq = {

    val entries : NodeSeq = Training.getWithParticipantCount.flatMap({
      training => bind("train",
                   chooseTemplate("training", "entry", xhtml),
                   "name" -> training.name
//                   "participantcount" -> training.participantCount,
//                   "viewparticipants" -> { SHtml.ajaxButton("view participants", 
//                                           () => viewParticipants(training.id, training.participantCount)) }
                  )
    })

    System.out.println("test: " + entries)
    bind("training", xhtml, "entry" -> entries)

  }
  
  def listTrainings2 = {
    
    ".training *" #> Training.getWithParticipantCount.map(training => 
      ".name" #> training.name &
      ".participantCount" #> training.participantCount
    )
  }
  
}
