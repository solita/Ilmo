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