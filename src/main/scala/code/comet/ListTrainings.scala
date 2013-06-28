package code.comet 

import net.liftweb.util._
import java.util.Date
import net.liftweb.http.js.JsCmds._
import _root_.scala.xml.Text
import scala.xml.NodeSeq
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.http.{S, SHtml}
import code.model.TrainingSession
import net.liftweb.http.js.JsCmd
import code.util.DateUtil
import code.model.TrainingSessionParticipantCountDto
import DataCenter._
import org.joda.time.DateTime
import net.liftweb.http.js.JE.Call
import net.liftweb.http.js.JE.Str

class ListTrainings extends CometActor with CometListener {
  
  private val showTrainingsSinceMonths = 3;
  private val pagesize = 6
  private val pager = new TablePaginator
  private val cityFilters = new CityFilters
  
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
    val trainings = getTrainingList.filter(t => cityFilters.matches(t.place))
    val futureTrainingCount = trainings.filter(_.date.after(TimeHelpers.now)).length
    
    // future trainings must be visible.. always..  
    val newPagesize = scala.math.max(futureTrainingCount, pagesize) 
    val totalCount = trainings.length
    pager.changeCounts(totalCount, newPagesize)
    
    val visibleTrainings = trainings.drop(pager.getIndexOfFirstVisibleRow-1).take(pager.getPageSize)
    
    "#paginator *" #> pager.buildPaginatorButtons &
    "#cityfilters *" #> cityFilters.getFilterLinks &
    ".training *" #> visibleTrainings.map(training =>
      ".name *" #> SHtml.a(() => viewDetails(training), Text(training.name)) & 
      ".place *" #> training.place &
      ".date *" #> formatTrainingInterval(training) &
      ".participantCount" #> training.participantCount &
      ".maxParticipants" #> training.maxParticipants &
      ".register *" #> ( getRegisterButton(training) ) &
      ".addtocalendar *" #> addToCalendarLink(training.id)
    )
  }
  
  private def formatTrainingInterval(trainingSession: TrainingSessionParticipantCountDto) = {
    DateUtil.formatInterval(trainingSession.date, trainingSession.endDate)    
  }
  
  private def addToCalendarLink(trainingId: Long) =
    SHtml.ajaxButton(S ?? "add.to.calendar", () => RedirectTo("api/cal/"+trainingId))
    
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
      if ( training.participantCount >= training.maxParticipants && DataCenter.hasCurrentUserName() ) { 
        SHtml.ajaxButton(S ?? "training.full", () => register(training.id))
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
  
  def viewDetails(trainingSession: TrainingSessionParticipantCountDto) : JsCmd = {
      DataCenter.setSelectedTrainingSession(trainingSession.id)
      Call("highlightTraining", Str(trainingSession.name), Str(trainingSession.place),
          Str(formatTrainingInterval(trainingSession)))
  }
  
  class CityFilters {
    abstract class CityFilter(localizedName: String, predicate: String=>Boolean) {
      def matches(city: String): Boolean = predicate(city)
      def me: CityFilter = this
      def nameText: NodeSeq = <span>{localizedName}</span>
      def buildLink() = 
        if (selectedFilter == me) nameText else SHtml.a(() => {selectedFilter = me; reRender}, nameText)
    }
    case object All extends CityFilter(S ?? "cityfilter.all", _ => true)
    case object Tre extends CityFilter(S ?? "cityfilter.tre", _.contains(S ?? "cityfilter.tampere"))
    case object Hki extends CityFilter(S ?? "cityfilter.hki", _.contains(S ?? "cityfilter.helsinki"))
    
    private var selectedFilter: CityFilter = All;
    private var filters = List(All, Tre, Hki)
    
    def getFilterLinks: NodeSeq = filters.flatMap(_.buildLink)
    def matches(city: String): Boolean = selectedFilter.matches(city)
  }
  
  class TablePaginator {

    private var pagesize = 3 
    private var count = 0
    private var visiblePage = 0
    
    def changeCounts(newCount: Int, newPagesize: Int) = {
      pagesize = newPagesize
      count = newCount
      // jos koulutuksia filteröidään, poistetaan tai lisätään niin mitä tehdään sivutukselle?
      if (visiblePage >= pageCount) visiblePage = 0
    } 
    def getPageSize = pagesize
    def getIndexOfFirstVisibleRow = visiblePage * pagesize + 1
    def getIndexOfLastVisibleRow = scala.math.min(getIndexOfFirstVisibleRow + pagesize - 1, count)
    
    def buildPaginatorButtons: NodeSeq = 
      if (count < pagesize) Text("") else prevLink ++ statusSpan ++ nextLink
    
    private def prevImg = <img class="prev" src="/images/prev.png" />
    private def nextImg = <img class="next" src="/images/next.png" />
    private def showingFirstPage = visiblePage == 0
    private def pageCount = scala.math.ceil(count.toDouble/pagesize).toInt
    private def showingLastPage = (1+visiblePage) == pageCount
    
    private def prevLink =
      if ( showingFirstPage ) prevImg else SHtml.a(() => {visiblePage -= 1; reRender}, prevImg)

    private def nextLink =
      if ( showingLastPage ) nextImg else SHtml.a(() => {visiblePage += 1; reRender}, nextImg)

    private def statusSpan = Text("%s - %s / %s".format(
          getIndexOfFirstVisibleRow, getIndexOfLastVisibleRow, count))

  }

}

