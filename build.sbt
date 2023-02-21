// See README.md for license details.

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "edu.duke.cs.apex"

val chiselVersion = "3.5.6"

lazy val berkeley_hf = RootProject(uri("https://github.com/Entropy-xcy/berkeley-hardfloat.git"))
//lazy val fpnew = RootProject(uri("git@github.com:Composer-Team/fpnew-wrapper.git"))

lazy val root = (project in file("."))
  .settings(
    name := "duke-strongfloat",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % chiselVersion,
      "edu.berkeley.cs" %% "chiseltest" % "0.5.4" % "test",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-P:chiselplugin:genBundleElements",
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full),
  ) dependsOn(berkeley_hf)
