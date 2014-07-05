import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "signup"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "jp.t2v" %% "play2.auth" % "0.9",
    "jp.t2v" %% "play2.auth.test" % "0.9" % "test",
    "com.typesafe" %% "play-plugins-mailer" % "2.1.0",
    "com.newrelic.agent.java" % "newrelic-api" % "2.19.+",
    "commons-lang" % "commons-lang" % "2.6",
    jdbc,
    anorm
  )

  // Only compile the LESS files listed here. Others will be included by the top ones.
  def customLessEntryPoints(base: File): PathFinder = (
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less")
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
    lessEntryPoints <<= baseDirectory(customLessEntryPoints),
    scalacOptions += "-feature"
  )
}
