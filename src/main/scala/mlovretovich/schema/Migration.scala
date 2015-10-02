package mlovretovich.schema

import org.flywaydb.core.Flyway
import sbt._
import sbt.Keys._
import Def.Initialize

import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.info.MigrationInfoDumper

object Migration {

  class MigrationRunner(connections: Seq[DataSource]) {

	val flyway = new Flyway
	def migrate = {
	  connections.foreach {c => flyway.configureDataSource(c).migrate }
	}
	def info = {
	  connections.map {c=> s"${c.schema}:\n" + MigrationInfoDumper.dumpToAsciiTable(flyway.configureDataSource(c).info.all) }.mkString("\n")
	}
  }

  def apply(config: Seq[DataSource]) =  {
	new MigrationRunner(config)
  }

  private implicit class FlywayOps(val flyway: Flyway) extends AnyVal {
	def configureDataSource(ds: DataSource): Flyway = {
	  flyway.setSchemas(ds.schema)
	  flyway.setDataSource(ds.toUrl, ds.user, ds.password)
	  flyway.setBaselineVersionAsString("1")
	  flyway.setSqlMigrationPrefix("")
	  flyway.setLocations(s"classpath:db/migration/${ds.schema}")
	  flyway
	}
  }

}
