import sbt._
import sbt.Keys._

object SchemaManagerBuld extends Build {

  lazy val project = Project (
	"project",
	file ("."),
	settings = Seq(
	  sbtPlugin := true,
	  name := "sbt-schema-migrate",
	  organization := "mlovretovich",
	  version := "0.1-SNAPSHOT",
	  resolvers += "Flyway" at "http://flywaydb.org/repo",
	  libraryDependencies += "org.flywaydb" % "flyway-core" % "3.2.1",
		libraryDependencies += "com.typesafe" % "config" % "1.3.0"
	)
  )
}