name := """signup"""

version := "1.0-SNAPSHOT"


lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "jp.t2v" %% "play2-auth"      % "0.13.+",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.+",
  "commons-lang" % "commons-lang" % "2.6",
  "se.crisp" %% "cloudinary-scala-play" % "0.9.5b-SNAPSHOT",
  "org.json" % "json" % "20140107",
  "com.newrelic.agent.java" % "newrelic-agent" % "3.8.2",
  "com.newrelic.agent.java" % "newrelic-api" % "3.8.2",
  "org.apache.poi" % "poi" % "3.10-FINAL",
  "org.apache.poi" % "poi-ooxml" % "3.10-FINAL",
  "com.netaporter" %% "scala-uri" % "0.4.2",
  "com.nimbusds" % "nimbus-jose-jwt" % "3.1.2",
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "2.3.1",
  jdbc,
  anorm,
  ws,
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "jp.t2v" %% "play2-auth-test" % "0.13.+" % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

includeFilter in (Assets, LessKeys.less) := "*.less"

scalacOptions ++= Seq("-target:jvm-1.7", "-feature")

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

herokuAppName in Compile := "signup-ci-test"


