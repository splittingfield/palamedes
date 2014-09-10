organization := "io.mental"

version := "0.0.1"

name := "palamedes"

scalaVersion := "2.10.4"

resolvers += "boundary" at "http://maven.boundary.com/artifactory/repo"

libraryDependencies ++= Seq(
  "com.boundary" % "high-scale-lib" % "1.0.3",
"org.scalatest" % "scalatest_2.10" % "2.2.0" % "test" withSources() withJavadoc(),
"org.scalacheck" %% "scalacheck" % "1.10.0" % "test" withSources() withJavadoc(),
"com.rojoma" % "rojoma-json-v3_2.10" % "3.1.2" withSources() withJavadoc(),
"org.apache.commons" % "commons-lang3" % "3.3.2",
"joda-time" % "joda-time" % "2.1",
 "org.joda" % "joda-convert" % "1.7",
"org.mockito" % "mockito-core" % "1.9.5" withSources() withJavadoc(),
"com.twitter" % "algebird_2.10" % "0.7.0" withSources() withJavadoc(),
"com.twitter" % "algebird-core_2.10" % "0.7.0" withSources() withJavadoc(),
"com.twitter" % "algebird-util_2.10" % "0.7.0" withSources() withJavadoc()
  )


testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")


scalacOptions ++= Seq("-optimize","-deprecation","-feature")
