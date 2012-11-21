import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "SimpleRadio",
    version := "0.1",
    versionCode := 0,
    scalaVersion := "2.10.0-RC2",
    platformName in Android := "android-14",
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    scalacOptions ++= Seq("-deprecation")
  )

  val proguardSettings = Seq (
    useProguard in Android := true,
    proguardOption in Android := "-keep class scala.Function1"
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me",
      libraryDependencies += "org.scalatest" % "scalatest_2.10.0-RC2" % "1.8" % "test"
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
