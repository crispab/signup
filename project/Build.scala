import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "signup"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "jp.t2v" %% "play2-auth" % "0.11.+",
    "jp.t2v" %% "play2-auth-test" % "0.11.+" % "test",
    "com.typesafe" %% "play-plugins-mailer" % "2.2.+",
    "commons-lang" % "commons-lang" % "2.6",
    "com.cloudinary" %% "cloudinary-scala-play" % "0.9.3-SNAPSHOT",
    "org.json" % "json" % "20140107",
    "com.newrelic.agent.java" % "newrelic-agent" % "3.8.2",
    "com.newrelic.agent.java" % "newrelic-api" % "3.8.2",
    "org.apache.poi" % "poi" % "3.10-FINAL",
    "org.apache.poi" % "poi-ooxml" % "3.10-FINAL",
    "com.netaporter" %% "scala-uri" % "0.4.2",
    "com.nimbusds" % "nimbus-jose-jwt" % "3.1.2",
    jdbc,
    anorm
  )

  // Only compile the LESS files listed here. Others will be included by the top ones.
  def customLessEntryPoints(base: File): PathFinder
    = (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
      (base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less")

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers ++= Seq("t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
                      "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
                      "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"),
    lessEntryPoints <<= baseDirectory(customLessEntryPoints),
    scalacOptions += "-feature"
  )
}
