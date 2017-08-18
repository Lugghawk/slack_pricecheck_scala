lazy val root = (project in file(".")).
  settings(
      name := "slack_pricecheck",
      version := "1.0",
      scalaVersion := "2.11.8"
    )

libraryDependencies += "com.github.gilbertw1" %% "slack-scala-client" % "0.1.8"
libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.4"
libraryDependencies += "com.netaporter" %% "scala-uri" % "0.4.14"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test

enablePlugins(JavaAppPackaging)
scalacOptions ++= Seq("-feature")
