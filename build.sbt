organization := "io.mental"

version := "0.0.1"

name := "palamedes"

scalaVersion := "2.10.2"

resolvers += "boundary" at "http://maven.boundary.com/artifactory/repo"

libraryDependencies ++= Seq(
  "com.boundary" % "high-scale-lib" % "1.0.3",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "optional"
  )


testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")


scalacOptions ++= Seq("-optimize","-deprecation","-feature")
