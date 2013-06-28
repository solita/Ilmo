package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import common._
import http._
import sitemap._
import sitemap.Loc.Link
import Loc._
import mapper._
import code.model._
import net.liftweb.http.provider.HTTPRequest
import java.util.Locale
import java.util.ResourceBundle
import code.model.calendar.CalendarICSFileHelper
import code.util.IlmoDateFormatter
import net.liftweb.widgets.sparklines.Sparklines


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    
    
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = new IlmoDBVendor(
          getProp("db.driver") openOr "org.h2.Driver", 
        getProp("db.url") openOr "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
      getProp("db.user"),
      getProp("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
      
    }
    
    def getProp(key: String): Box[String] = {
      sys.props.get(key) match {
        case Some(value) => Full(value)
        case None => Props.get(key)
      }
    }
        
    def createDb: Boolean = {
      getProp("db.createschema").isDefined || getProp("db.driver") === "org.h2.Driver";
    }

    if (createDb) {
      // Use Lift's Mapper ORM to populate the database
      Schemifier.schemify(true, Schemifier.infoF _, Training, Participant, TrainingSession)
    }

    MapperRules.displayNameCalculator.default.set({(m : BaseMapper, l : Locale, s : String) => S ?? (m.getClass().getSimpleName().toLowerCase() + "." + s)})
    
    // where to search snippet
    LiftRules.addToPackages("code")
    
    // under edit we could have list-, edit- and confirm delete -html-pages
    val editTrainingPages = new Link("edit_training" :: Nil, true)
    val editTrainingSessionPages = new Link("edit_trainingsession" :: Nil, true)
      
    def sitemap() = SiteMap(
      Menu(S ?? "trainings") / "index",
      Menu(S ?? "training.add") / "add_training",
      Menu(S ?? "trainingsession.add") / "add_training_session",
      // TODO toimiiko wildcardit, menisi siistimmin?
      Menu(Loc("edit_training", editTrainingPages, S ?? "training.edit")),
      Menu(Loc("edit_trainingsession", editTrainingSessionPages, S ?? "trainingsession.edit"))
    )
    
    LiftRules.setSiteMap(sitemap)

    // Use jQuery 1.4
    LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      Html5Properties(r.userAgent))
      
    LiftRules.liftCoreResourceName = "content"
    
    // TODO: Proper localization handling
    LiftRules.localeCalculator = _ => new Locale("fi", "FI")
    
    // TODO: Proper logging
    LiftRules.localizationLookupFailureNotice = Full((key,locale) => println("No translation for %s exists for %s".format(key,locale)))

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
    
    // stateful — associated with a servlet container session
    LiftRules.dispatch.append(CalendarICSFileHelper)
    
    LiftRules.dateTimeConverter.default.set(() => new IlmoDateFormatter())
    
  }
}
