import sbt._

import Keys._
import AndroidKeys._

object General {

  val settings = Defaults.defaultSettings ++ Seq (
    name := "SimpleRadio",
    version := "0.1",
    versionCode := 0,
    scalaVersion := "2.10.0",
    platformName in Android := "android-14",
     resolvers += (
      "Edge System Design Repository" at "http://jvmrepo.edgesysdesign.com/"
    ),
    libraryDependencies ++= Seq(
      "com.edgesysdesign" %% "frequency" % "master",
      "org.scalatest" % "scalatest_2.10.0-RC3" % "1.8-B1" % "test",
       "org.scaloid" % "scaloid" % "1.1_8_2.10"
    ),
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-deprecation",
      "-feature")
  )

  val proguardSettings = Seq (
    useProguard in Android := true,
    proguardOption in Android := """
      -keep class scala.Function1
      -keep class scala.collection.SeqLike { public protected *; }
    """
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me"
    )
}

object AndroidBuild extends Build {
  lazy val main = Project (
    "SimpleRadio",
    file("."),
    settings = General.fullAndroidSettings
  )

  lazy val tests = Project (
    "tests",
    file("tests"),
    settings = General.settings ++
               AndroidTest.androidSettings ++
               General.proguardSettings ++ Seq (
      name := "SimpleRadioTests"
    )
  ) dependsOn main
}
