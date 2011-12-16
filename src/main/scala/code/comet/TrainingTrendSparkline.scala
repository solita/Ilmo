package code.comet
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import scala.xml.NodeSeq
import net.liftweb.http.js.JE.JsArray
import net.liftweb.http.js.JE.JsObj
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.widgets.sparklines.Sparklines
import net.liftweb.widgets.sparklines.SparklineStyle

class TrainingTrendSparkline extends CometActor with CometListener {
  
    def registerWith = DataCenter
  
    override def lowPriority = {
      case TrainingsChanged => reRender
      case msg if msg.isInstanceOf[StateChanged] => Noop
    }
    
    override def render = {
      // todo hae kannasta data
      /* Oraclessa
       * with months as (select add_months(sysdate, -level+1) as m from dual connect by level <= 12)
           select m.m, (select count(*) from trainingsession where to_char(date_c,'mmyyyy')=to_char(m.m,'mmyyyy'))
           from months m; 
       */
      val data = JsArray(100,500,300,200,400,500,400,400,
                       100,200, 345, 412, 111, 234, 490);
      
      // todo värit ei toimi
      val opts = JsObj(("zeroAxis" -> false),
                       ("barColor" -> "blue"),
                       ("nullColor" -> "red"),
                       ("background" -> "white"));
      Sparklines.onLoad("bar", SparklineStyle.BAR, data, opts);
    }
    
}