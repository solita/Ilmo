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
import code.model.Training
import net.liftweb.http.js.JsCmd
import net.liftweb.widgets.tablesorter.{TableSorter, DisableSorting, Sorting, Sorter}


class EditTrainings {
  
  val headers = (0, DisableSorting()) :: Nil
  val sortList = (1,Sorting.DSC) :: Nil
  
  val options = TableSorter.options(headers,sortList)
  
//  def render(xhtml: NodeSeq): NodeSeq = {
//    
//  }     
  
   def listTrainings = {
    //val tablesorter = TableSorter("#traininglist", options)
//    TableSorter.renderOnLoad("#traininglist", options)
//    
//    TableSorter("#traininglist", options)
    
    ".training *" #>  Training.getWithParticipantCount.map(training => 
      ".name" #> training.name &
      ".participantCount" #> training.participantCount
    ) 
    
  }
  
  def tablesort = {
    TableSorter("#traininglist", options)
  }
  
}

