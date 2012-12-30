import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "signup"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "jp.t2v" %% "play20.auth" % "0.5",
    "com.typesafe" %% "play-plugins-mailer" % "2.0.4"
  )

  // Only compile the LESS files listed here. Others will be included by the top ones.
  def customLessEntryPoints(base: File): PathFinder = (
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less")
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
    lessEntryPoints <<= baseDirectory(customLessEntryPoints)
  )
}
