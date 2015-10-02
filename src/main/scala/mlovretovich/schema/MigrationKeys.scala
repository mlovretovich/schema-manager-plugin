package mlovretovich.schema

import sbt._
import Keys._

trait MigrationKeys {
  lazy val DB = config("db") extend(Compile)

  lazy val migrate = taskKey[Unit]("Migrates the schema to the latest version'")
  lazy val info = taskKey[Unit]("Displays info about the current state of the schema.")
  lazy val schemaManagerConnections = settingKey[Map[String,Seq[String]]]("Map of the schemas and connections to manage. $schema->$connection")
  lazy val migrationConfigFileName = settingKey[File]("migrate")
  lazy val migrationConfig = settingKey[Seq[DataSource]]("data source to use when tests are triggered")
  lazy val schemaDependencies = settingKey[Seq[String]]("Schemas automatically prepended to the list of schemas managed per connection")
}

object MigrationKeys extends MigrationKeys