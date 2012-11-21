import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "signup"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "joda-time" % "joda-time" % "2.1",
    "jp.t2v" %% "play20.auth" % "0.3-SNAPSHOT",
    "com.typesafe" %% "play-plugins-mailer" % "2.0.4"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/"
  )

}
