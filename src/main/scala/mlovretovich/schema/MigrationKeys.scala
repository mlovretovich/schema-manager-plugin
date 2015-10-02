package mlovretovich.schema

import sbt._
import Keys._

trait MigrationKeys {
  lazy val DB = config("db") extend(Compile)

  lazy val migrate = taskKey[Unit]("Migrates the schema to the latest version'")
  lazy val info = taskKey[Unit]("Displays info about the current state of the schema.")
  lazy val migrationConfigFileName = settingKey[File]("migrate")
  lazy val migrationConfig = settingKey[Seq[DataSource]]("data source to use when tests are triggered")
  lazy val configPrefix = settingKey[String]("the config prefix for the migration datasource")
  lazy val configKeys = settingKey[Seq[String]]("config keys for migration")
  lazy val migrationConfigKeys = settingKey[Seq[String]]("config keys for migration")
  lazy val SchemasToMigrate = settingKey[Seq[DataSource]]("")
}

object MigrationKeys extends MigrationKeys