package code.util

object UrlFormatter {
  
  def addUrlProtocolIfNecessary(url: String) = {
    if(url != null && url != "" && !url.contains("://")) {
      "http://" + url
    } else {
      url
    }
  }
  
}