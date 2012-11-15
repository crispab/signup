import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "signup"
  val appVersion = "1.0-SNAPSHOT"

  val ssDependencies = Seq(
    // Add your project dependencies here,
    "com.typesafe" %% "play-plugins-util" % "2.0.3",
    "org.mindrot" % "jbcrypt" % "0.3m"
  )

  val secureSocial = PlayProject(
    "securesocial", appVersion, ssDependencies, mainLang = SCALA, path = file("modules/securesocial")
  ).settings(
    resolvers ++= Seq(
      "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
    )
  )

  val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "joda-time" % "joda-time" % "2.1",
    "jp.t2v" %% "play20.auth" % "0.3-SNAPSHOT",
    "com.typesafe" %% "play-plugins-mailer" % "2.0.4"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/"
  ).dependsOn(secureSocial).aggregate(secureSocial)

}
