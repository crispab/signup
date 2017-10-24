name := """signup"""

version := "1.0-SNAPSHOT"


lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" %  "httpclient"            % "4.3.+",
  "org.apache.httpcomponents" %  "httpcore"              % "4.3.+",
  "postgresql"                %  "postgresql"            % "9.2-1002.jdbc4",
  "jp.t2v"                    %% "stackable-controller"  % "0.6.0",
  "jp.t2v"                    %% "play2-auth"            % "0.14.2",
  "com.typesafe.play"         %% "play-mailer"           % "2.4.+",
  "commons-lang"              %  "commons-lang"          % "2.6",
  "com.cloudinary"            %% "cloudinary-scala-play" % "0.9.7-SNAPSHOT",
  "org.json"                  %  "json"                  % "20140107",
  "org.apache.poi"            %  "poi"                   % "3.10-FINAL",
  "org.apache.poi"            %  "poi-ooxml"             % "3.10-FINAL",
  "com.netaporter"            %% "scala-uri"             % "0.4.2",
  "com.nimbusds"              %  "nimbus-jose-jwt"       % "3.1.2",
  "org.webjars"               %% "webjars-play"          % "2.3.0-2",
  "org.webjars"               %  "jquery"                % "2.1.4",
  "org.webjars"               %  "bootstrap"             % "3.3.4",
  "org.webjars"               %  "font-awesome"          % "4.3.0-2",
  "com.typesafe.play"         %% "anorm"                 % "2.4.0",
  jdbc,
  evolutions,
  ws,
  "org.scalatestplus"         %% "play"                   % "1.2.+"       % "test",
  "junit"                     %  "junit"                  % "4.12"        % "test",
  "info.cukes"                %  "cucumber-java"          % "1.2.+"       % "test",
  "info.cukes"                %  "cucumber-junit"         % "1.2.+"       % "test",
  "info.cukes"                %  "cucumber-picocontainer" % "1.2.+"       % "test",
  "org.seleniumhq.selenium"   %  "selenium-java"          % "2.45.+"      % "test",
  "com.codeborne"             %  "phantomjsdriver"        % "1.2.1"       % "test", // temporary, until fixed https://github.com/detro/ghostdriver/issues/397
  //"com.github.detro" % "phantomjsdriver" % "1.2.0" % "test"
  "org.ocpsoft.prettytime"    %  "prettytime-nlp"         % "4.0.0.Final" % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
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