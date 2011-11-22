

object WebServer extends Application {
  /*
  val server = new Server
  val scc = new SelectChannelConnector
  scc.setPort(7777)
  server.setConnectors(Array(scc))

  val context = new WebAppContext()
  context.setServer(server)
  context.setContextPath("/")
  context.setResourceBase("src/main/webapp")
  context.setWar("src/main/webapp")
  server.setHandler(context);
  // server._handler = context
  
  try {
    println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP")
    server.start()
    while (System.in.available() == 0) {
      Thread.sleep(5000)
    }
    server.stop()
    server.join()
  } catch {
    case exc: Exception => {
      exc.printStackTrace()
      System.exit(100)
    }
  }
  */
}