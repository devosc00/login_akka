import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "login_module"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "com.typesafe.play" %% "play-slick" % "0.4.0" ,
    "jp.t2v" %% "play2.auth" % "0.10.1",
    "jp.t2v" %% "play2.auth.test" % "0.10.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.0" % "test"

  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
