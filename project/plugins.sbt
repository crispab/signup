// Comment to get more information during initialization
logLevel := Level.Warn


addSbtPlugin("com.typesafe.play" % "sbt-plugin"    % "2.6.12")
addSbtPlugin("com.eed3si9n"      % "sbt-buildinfo" % "0.7.0")

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs"  % "1.0.10")


// Heroku plugin
addSbtPlugin("com.heroku"       % "sbt-heroku"   % "2.1.1")
addSbtPlugin("com.timushev.sbt" % "sbt-updates"  % "0.3.4")
//addSbtPlugin("io.get-coursier"  % "sbt-coursier" % "1.0.2")

