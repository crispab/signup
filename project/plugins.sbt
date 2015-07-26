// Comment to get more information during initialization
logLevel := Level.Warn

// The Play plugin
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.8")

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")


// Heroku plugin
addSbtPlugin("com.heroku" % "sbt-heroku" % "0.3.5")
