name := """signup"""

version := "1.0-SNAPSHOT"


lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.3.+",
  "org.apache.httpcomponents" % "httpcore" % "4.3.+",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "jp.t2v" %% "stackable-controller" % "0.4.+",
  "jp.t2v" %% "play2-auth"      % "0.13.+",
  "com.typesafe.play" %% "play-mailer" % "2.4.+",
  "commons-lang" % "commons-lang" % "2.6",
  "com.cloudinary" %% "cloudinary-scala-play" % "0.9.7-SNAPSHOT",
  "org.json" % "json" % "20140107",
  "com.newrelic.agent.java" % "newrelic-agent" % "3.8.2",
  "com.newrelic.agent.java" % "newrelic-api" % "3.8.2",
  "org.apache.poi" % "poi" % "3.10-FINAL",
  "org.apache.poi" % "poi-ooxml" % "3.10-FINAL",
  "com.netaporter" %% "scala-uri" % "0.4.2",
  "com.nimbusds" % "nimbus-jose-jwt" % "3.1.2",
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.3.2",
  "org.webjars" % "font-awesome" % "4.3.0-1",
  jdbc,
  anorm,
  ws,
  "org.scalatestplus" %% "play" % "1.2.+" % "test",
  "junit" % "junit" % "4.12" % "test",
  "info.cukes" % "cucumber-java" % "1.2.+" % "test",
  "info.cukes" % "cucumber-junit" % "1.2.+" % "test",
  "info.cukes" % "cucumber-picocontainer" % "1.2.+" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.45.+" % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

includeFilter in (Assets, LessKeys.less) := "*-bootstrap.less"

scalacOptions ++= Seq("-target:jvm-1.7", "-feature")

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

herokuAppName in Compile := "signup-ci-test"


