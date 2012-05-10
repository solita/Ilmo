package code.comet 

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
import code.model.{Training, TrainingSession}
import net.liftweb.http.js.JsCmd
import code.util.DateUtil
import code.model.TrainingSessionParticipantCountDto
import java.util.Calendar
import DataCenter._
import org.joda.time.DateTime
import net.liftweb.http.js.JE.Call
import net.liftweb.http.js.JsExp
import net.liftweb.http.js.JE.Num
import net.liftweb.http.js.JE.JsRaw
import scala.xml.Elem
import scala.xml.Node

class ListTrainings extends CometActor with CometListener {
  
  private var showTrainingsSinceMonths = 3;
  private val pagesize = 6
  private val pager = new TablePaginator
  
  def registerWith = DataCenter

  override def lowPriority = {
    case UserSignedIn(name) if isMyUser(name) => reRender // todo optimize
    case UserSignedOut(name) if wasMyUser(name) => reRender // todo optimize
    case NewParticipant(name, tId) => reRender
    case DelParticipant(name, tId) => reRender
    case TrainingsChanged => reRender
    case msg if ilmomsg(msg) => Noop
  }

  override def render = {
    var trainings = getTrainingList
    val futureTrainingCount = trainings.filter(_.date().after(TimeHelpers.now)).length
    
    pager.setCount(trainings.length)
    // upcoming training must be visible.. always..  
    pager.setPageSize(scala.math.max(futureTrainingCount, pagesize)) 
    
    trainings = trainings.drop(pager.getIndexOfFirstVisibleRow-1).take(pager.getPageSize)
    
    "#paginator *" #> pager.buildPaginatorButtons &
    ".training *" #> trainings.map(training => 
      ".name *" #> SHtml.a(() => viewDetails(training.id), Text(training.name)) & 
      ".place *" #> training.place &
      ".date *" #> DateUtil.formatInterval(training.date, training.endDate) &
      ".participantCount" #> training.participantCount &
      ".maxParticipants" #> training.maxParticipants &
      ".register *" #> ( getRegisterButton(training) ) &
      ".addtocalendar *" #> addToCalendarLink(training.id)
    )
  }
  
  private def addToCalendarLink(trainingId: Long) =
    <a title={S ?? "add.to.calendar"} href={"api/cal/"+trainingId}>
        <img src="/images/Calendar-Add-16.png" /> {S ?? "add.to.calendar"}
    </a>
    
  private def getTrainingList = {
    if ( DataCenter hasCurrentUserName ) 
      TrainingSession.getWithParticipantCountForParticipantId(
          DataCenter getCurrentUserName(), trainingsSinceDate) 
    else 
      TrainingSession.getWithParticipantCount(trainingsSinceDate)
  }
  
  private def trainingsSinceDate = {
    (new DateTime).minusMonths(showTrainingsSinceMonths).toDateMidnight().toDate()
  }
  
  def getRegisterButton(training: TrainingSessionParticipantCountDto) = {
    if ( training.date.before(new Date) ) {
      Text(S ?? "past.training")
    }
    else if ( training.hasSignedInUserParticipated ) {
      SHtml.ajaxButton(S ?? "training.unregister", () => unregister(training.id))
    }
    else {
      if ( training.participantCount >= training.maxParticipants ) { 
        Text(S ?? "training.full")
      }
      else if ( !DataCenter.hasCurrentUserName() ) {
        <button type="button" disabled="disabled">{S ?? "training.register"}</button>  
      }
      else {
        SHtml.ajaxButton(S ?? "training.register", () => register(training.id))
      }
    }
  }
      
  def register(trainingId: Long) : JsCmd = {
      DataCenter ! NewParticipant(DataCenter getCurrentUserName, trainingId)
  }

  def unregister(trainingId: Long) : JsCmd = {
      DataCenter ! DelParticipant(DataCenter getCurrentUserName, trainingId)
  }
  
  def viewDetails(trainingSessionId: Long) : JsCmd = {
      DataCenter.setSelectedTrainingSession(trainingSessionId)
  }
  
  class TablePaginator {

    private var pagesize = 3; 
    private var firstrow = 1;
    private var count = 0;
    
    def setCount(c: Int) = count = c 
    def setPageSize(p: Int) = pagesize = p
    def getPageSize = pagesize
    def getIndexOfFirstVisibleRow = firstrow
    
    def buildPaginatorButtons: NodeSeq = 
      if (count < pagesize) Text("") else prevLink ++ statusSpan ++ nextLink
    
    private def prevImg = <img class="prev" src="/images/prev.png" />
    private def nextImg = <img class="next" src="/images/next.png" />
    private def showingFirstPage = firstrow == 1
    private def showingLastPage = firstrow + pagesize > count
    
    private def prevLink =
      if ( showingFirstPage ) prevImg else SHtml.a(() => {firstrow -= pagesize; reRender}, prevImg)

    private def nextLink =
      if ( showingLastPage ) nextImg else SHtml.a(() => {firstrow += pagesize; reRender}, nextImg)

    private def statusSpan = <span>{"%s - %s / %s".format(
          firstrow, scala.math.min(firstrow + pagesize - 1, count), count)}</span>

  }

}

