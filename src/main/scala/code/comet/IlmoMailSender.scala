package code.comet

import net.liftweb.util.Mailer
import scala.xml.NodeSeq
import net.liftweb.util.Mailer.From
import net.liftweb.util.Mailer.Subject
import net.liftweb.util.Mailer.To
import net.liftweb.util.Mailer.PlainMailBodyType
import java.util.Date
import code.util.DateUtil

object IlmoMailSender {
  
    val VARASIJALTA_OSALLISTUJAKSI_SUBJECT = "Pääsit koulutukseen %s!"
    val VARASIJALTA_OSALLISTUJAKSI_MSG = "%s perui ilmoittautumisensa, joten pääsit mukaan koulutukseen %s %s."
    val FROM_ADDRESS = From("ilmo@solita.fi")
      
    def notifyVarasijaltaOsallistujaksi(removedParticipantName: String, newParticipantName: String, 
                                        trainingName: String, trainingStartTime: Date) {
      send(VARASIJALTA_OSALLISTUJAKSI_SUBJECT.format(trainingName), 
           "janne.rintanen@solita.fi", //getEmailAddress(newParticipantName), 
           VARASIJALTA_OSALLISTUJAKSI_MSG.format(removedParticipantName, trainingName, DateUtil.formatDateTime(trainingStartTime)))
    }
    
    def enabled = sys.props.get("mail.smtp.host").isDefined;
    
    def send(subjectStr: String, recipientAddress: String, plainContent : String) {
      try {
        if ( enabled ) {
          Mailer.sendMail(FROM_ADDRESS, Subject(subjectStr), To(recipientAddress), PlainMailBodyType(plainContent))
        }
        else {
          println("Sending email with subject " + subjectStr + ", to " + recipientAddress + " with content " + plainContent)
        }
      } catch {
        case e: Exception => println("sending email failed " + e);
      }
    }

    def getEmailAddress(participantFullName: String): String = {
        participantFullName.toLowerCase()
                           .replace(" ", ".")
                           .replace("ä", "a")
                           .replace("ö", "o")
                           .+("@solita.fi")
    } 
    
    def getMailAddressList(participantNames: Seq[String]): String = {
        participantNames.map(name => getEmailAddress(name)).mkString(";");
    } 
}
