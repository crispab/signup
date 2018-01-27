name := """signup"""
version := "4.6.0-SNAPSHOT"


lazy val root = (project in file(".")).
  enablePlugins(PlayScala, BuildInfoPlugin).settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "se.crisp.signup4"
  )

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" %  "httpclient"                  % "4.5.4",
  "org.apache.httpcomponents" %  "httpcore"                    % "4.4.9",
  "com.mohiva"                %% "play-silhouette"             % "4.0.0",
  "com.mohiva"                %% "play-silhouette-persistence" % "4.0.0",
  "com.mohiva"                %% "play-silhouette-crypto-jca"  % "4.0.0",
  "org.postgresql"            %  "postgresql"                  % "42.2.0",
  "jp.t2v"                    %% "stackable-controller"        % "0.6.0",
  "jp.t2v"                    %% "play2-auth"                  % "0.14.2",
  "com.typesafe.play"         %% "play-mailer"                 % "5.0.0",
  "org.apache.commons"        %  "commons-lang3"               % "3.7",
  "com.cloudinary"            %% "cloudinary-scala-play"       % "1.2.1",
  "org.json"                  %  "json"                        % "20171018",
  "org.apache.poi"            %  "poi"                         % "3.17",
  "org.apache.poi"            %  "poi-ooxml"                   % "3.17",
  "io.lemonlabs"              %% "scala-uri"                   % "0.5.1",
  "org.webjars"               %% "webjars-play"                % "2.5.0",
  "org.webjars"               %  "jquery"                      % "2.1.4",
  "org.webjars"               %  "bootstrap"                   % "3.3.4",
  "org.webjars"               %  "font-awesome"                % "4.3.0-2",
  "com.typesafe.play"         %% "anorm"                       % "2.5.3",
  "io.codekvast"              %  "codekvast-agent"             % "0.22.3",
  "org.codehaus.groovy"       %  "groovy-all"                  % "2.4.13",
  "com.iheart"                %% "ficus"                       % "1.2.7",
  "net.codingwell"            %% "scala-guice"                 % "4.1.1",

  jdbc,
  evolutions,
  ws,
  cache,

  "org.scalatestplus.play"    %% "scalatestplus-play"      % "2.0.1"       % "test",
  "org.mockito"               %  "mockito-core"            % "2.13.0"      % "test",
  "org.ocpsoft.prettytime"    %  "prettytime-nlp"          % "4.0.0.Final" % "test",
  "com.mohiva"                %% "play-silhouette-testkit" % "4.0.0"       % "test"
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