import sbt.Resolver

name := """sform2s"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.3"
resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"
resolvers += Resolver.jcenterRepo
resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

unmanagedBase := baseDirectory.value / "lib"

libraryDependencies ++= Seq(
  jdbc,
  ws,
  evolutions,
  "org.scala-lang" % "scala-reflect" % "2.13.3",
  "mysql" % "mysql-connector-java" % "8.0.20",
  "org.scalikejdbc" %% "scalikejdbc" % "3.4.2",
  "org.scalikejdbc" %% "scalikejdbc-config" % "3.4.2",
  "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.8.0-scalikejdbc-3.4",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.4.2",
  "org.scalikejdbc" %% "scalikejdbc-joda-time" % "3.4.2",
  "org.scalikejdbc" %% "scalikejdbc-test" % "3.4.2"  % "test",
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "7.0.0" % "test",
  "org.webjars" %% "webjars-play" % "2.8.0-1",
  "com.typesafe.play" %% "play-mailer" % "8.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "8.0.1",
  "net.codingwell" %% "scala-guice" % "4.2.10",
  "com.iheart" %% "ficus" % "1.4.7",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.4-akka-2.6.x",
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4",
  "com.digitaltangible" %% "play-guard" % "2.5.0",

  "org.apache.commons" % "commons-lang3" % "3.10",
  "commons-io" % "commons-io" % "2.7" ,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  ehcache,
  guice,
  filters
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

routesImport += "net.macolabo.sform2.utils.route.Binders._"

// https://github.com/playframework/twirl/issues/105
TwirlKeys.templateImports := Seq()

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Xlint:-unused,_"
)
