package mlovretovich.schema

import org.flywaydb.core.internal.util.logging.{LogFactory, LogCreator}

import sbt._
import Keys._
import plugins._
import sbt.classpath.ClasspathUtilities

object SchemaManagerPlugin extends AutoPlugin {

  object autoImport  extends MigrationKeys

  override def trigger = allRequirements
  override def requires = JvmPlugin

  import autoImport._

  lazy val testMigrationSettings = Seq(

	schemaDependencies := Seq("public","audit"),
	migrationConfigFileName in Test := (resourceDirectory in Test).value,
	migrationConfig  := MigrationConfig(
	  migrationConfigFileName.value,
	  schemaManagerConnections.value,
	  schemaDependencies.value
	),
	migrate <<= (migrationConfig, fullClasspath in Runtime, streams) map {
	  (m,c,s) => withPrepared(c,s){
		Migration(m).migrate
	  }
	},
	info <<=(migrationConfig, fullClasspath in Runtime, streams) map {
	  (m,c,s) => withPrepared(c,s){
		s.log.info(Migration(m).info)
	  }
	},
  	testGrouping <<= testGrouping dependsOn migrate
  )

  lazy val defaultMigrationSettings: Seq[Def.Setting[_]] = Seq(

	schemaDependencies := Seq("public","audit"),
	migrationConfigFileName := (resourceDirectory in Compile).value,
	migrationConfig  := MigrationConfig(
	  migrationConfigFileName.value,
	  schemaManagerConnections.value,
	  schemaDependencies.value
	),
	migrate <<= (migrationConfig, fullClasspath in Runtime, streams) map {
	  (m,c,s) => withPrepared(c,s){
		Migration(m).migrate
	  }
	},
	info <<=(migrationConfig, fullClasspath in Runtime, streams) map {
	  (m,c,s) => withPrepared(c,s){
		s.log.info(Migration(m).info)
	  }
	}
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] = {
	inConfig(DB)(defaultMigrationSettings) ++ inConfig(Test)(testMigrationSettings)
  }

  def withPrepared[T](cp: Types.Id[Keys.Classpath], streams: TaskStreams)(f: => T): T = {
	registerAsFlywayLogger(streams)
	withContextClassLoader(cp)(f)
  }

  /**
   * registers sbt log as a static logger for Flyway
   */
  private def registerAsFlywayLogger(streams: TaskStreams) {
	LogFactory.setLogCreator(SbtLogCreator)
	FlywaySbtLog.streams = Some(streams)
  }

  private def getContextClassLoader[T](cp: Types.Id[Keys.Classpath]) = {
	ClasspathUtilities.toLoader(cp.map(_.data), getClass.getClassLoader)
  }

  private def withContextClassLoader[T](cp: Types.Id[Keys.Classpath])(f: => T): T = {
	val classloader = ClasspathUtilities.toLoader(cp.map(_.data), getClass.getClassLoader)
	val thread = Thread.currentThread
	val oldLoader = thread.getContextClassLoader
	try {
	  thread.setContextClassLoader(classloader)
	  f
	} finally {
	  thread.setContextClassLoader(oldLoader)
	}
  }

  private object SbtLogCreator extends LogCreator {
	def createLogger(clazz: Class[_]) = FlywaySbtLog
  }

  private object FlywaySbtLog extends org.flywaydb.core.internal.util.logging.Log {
	var streams: Option[TaskStreams] = None
	def debug(message: String) { streams map (_.log.debug(message)) }
	def info(message: String) { streams map (_.log.info(message)) }
	def warn(message: String) { streams map (_.log.warn(message)) }
	def error(message: String) { streams map (_.log.error(message)) }
	def error(message: String, e: Exception) { streams map (_.log.error(message)); streams map (_.log.trace(e)) }
  }
}