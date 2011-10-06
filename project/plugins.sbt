resolvers += {
  val typesafeRepoUrl = new java.net.URL("http://repo.typesafe.com/typesafe/releases")
  val pattern = Patterns(false, "[organisation]/[module]/[sbtversion]/[revision]/[type]s/[module](-[classifier])-[revision].[ext]")
  Resolver.url("Typesafe Repository", typesafeRepoUrl)(pattern)
}

resolvers += {
  val typesafeRepoUrl = new java.net.URL("http://repo.typesafe.com/typesafe/ivy-snapshots")
  val pattern = Patterns(false, "[organisation]/[module]/[revision]/[type]s/[module](-[classifier]).[ext]")
  Resolver.url("Typesafe SNAPSHOTS", typesafeRepoUrl)(pattern)
}

resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.4.0")

addSbtPlugin("com.github.siasia" % "xsbt-web-plugin" % "0.1.2")