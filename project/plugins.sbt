// Comment to get more information during initialization
logLevel := Level.Warn


addSbtPlugin("com.typesafe.play" % "sbt-plugin"    % "2.5.18")
addSbtPlugin("com.eed3si9n"      % "sbt-buildinfo" % "0.7.0")

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs"  % "1.0.7")


// Heroku plugin
addSbtPlugin("com.heroku"       % "sbt-heroku"   % "0.3.5")
addSbtPlugin("com.timushev.sbt" % "sbt-updates"  % "0.3.3")
addSbtPlugin("io.get-coursier"  % "sbt-coursier" % "1.0.0-RC12")

