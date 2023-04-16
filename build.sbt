name := """signup"""
version := "4.8.2-SNAPSHOT"


lazy val root = (project in file(".")).
  enablePlugins(PlayScala, BuildInfoPlugin).settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "se.crisp.signup4"
  )

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" %  "httpclient"                      % "4.5.5",
  "org.apache.httpcomponents" %  "httpcore"                        % "4.4.9",
  "com.mohiva"                %% "play-silhouette"                 % "5.0.7",
  "com.mohiva"                %% "play-silhouette-persistence"     % "5.0.7",
  "com.mohiva"                %% "play-silhouette-crypto-jca"      % "5.0.7",
  "org.postgresql"            %  "postgresql"                      % "42.2.1",
  "com.typesafe.play"         %% "play-mailer"                     % "6.0.1",
  "com.typesafe.play"         %% "play-mailer-guice"               % "6.0.1",
  "org.apache.commons"        %  "commons-lang3"                   % "3.7",
  //"com.cloudinary"            %% "cloudinary-scala-play"           % "1.2.2",
  "org.json"                  %  "json"                            % "20180130",
  "org.apache.poi"            %  "poi"                             % "3.17",
  "org.apache.poi"            %  "poi-ooxml"                       % "3.17",
  "io.lemonlabs"              %% "scala-uri"                       % "0.5.7",
  "org.webjars"               %% "webjars-play"                    % "2.6.3",
  "org.webjars"               %  "jquery"                          % "2.1.4",
  "org.webjars"               %  "bootstrap"                       % "3.3.4",
  "org.webjars"               %  "font-awesome"                    % "4.3.0-2",
  "org.playframework.anorm"   %% "anorm"                           % "2.6.2",
  "com.typesafe.play"         %% "play-json"                       % "2.6.9",
  "com.typesafe.play"         %% "play-iteratees"                  % "2.6.1",
  "com.typesafe.play"         %% "play-iteratees-reactive-streams" % "2.6.1",
  "io.codekvast"              %  "codekvast-agent"                 % "0.22.3",
  "org.codehaus.groovy"       %  "groovy-all"                      % "2.4.14",
  "com.iheart"                %% "ficus"                           % "1.4.3",
  "net.codingwell"            %% "scala-guice"                     % "4.1.1",

  jdbc,
  evolutions, // Remove when co-existing with Signup6 that will take over managing the DB schema
  ws,
  cacheApi,
  guice,

  "org.scalatestplus.play"    %% "scalatestplus-play"      % "3.1.2"       % "test",
  "org.mockito"               %  "mockito-core"            % "2.15.0"      % "test",
  "org.ocpsoft.prettytime"    %  "prettytime-nlp"          % "4.0.0.Final" % "test",
  "com.mohiva"                %% "play-silhouette-testkit" % "5.0.7"       % "test",

  // Cloudinary stuff
  "com.ning" % "async-http-client" % "1.9.40",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.json4s" %% "json4s-ext" % "3.5.0"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.jcenterRepo
)

routesGenerator := InjectedRoutesGenerator

includeFilter in (Assets, LessKeys.less) := "*-bootstrap.less"

scalacOptions ++= Seq("-target:jvm-1.8", "-feature")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

herokuAppName in Compile := "signup-ci-test"

initialize := {
  val _ = initialize.value
  if (!(sys.props("java.specification.version") == "1.8" || sys.props("java.specification.version") == "9"))
    sys.error("Java 8 or 9 is required for this project. Found " + sys.props("java.specification.version"))
}


fork in run := true