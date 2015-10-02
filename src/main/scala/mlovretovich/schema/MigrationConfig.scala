package mlovretovich.schema

import sbt._
import sbt.Keys._
import com.typesafe.config.{Config, ConfigFactory}
import Def.Initialize


case class DataSource(host:String, databaseName: String, user: String, password:String, schema: String) {
  override def toString: String = host + ":" + databaseName + ":" + user + ":" + password + ":" + schema
  def toUrl = s"jdbc:postgresql://$host/$databaseName"
}

object  DataSource {

  def apply(directory: File, dsKeys: Seq[String]): Seq[DataSource] = {

	val configFile = System.getProperty("config.file", sys.env.getOrElse("CONFIG_FILE", ""))
	val scalaEnv = System.getProperty("scala.env", sys.env.getOrElse("SCALA_ENV", "local"))
	val config = ConfigFactory.defaultOverrides
	  .withFallback(ConfigFactory.parseFile(directory / s"$scalaEnv.conf"))
	  .withFallback(ConfigFactory.parseFile(directory / configFile))
	  .withFallback(ConfigFactory.parseFile(directory / "application.conf"))
	  .withFallback(ConfigFactory.defaultReference)

	dsKeys map ( k =>
	  DataSource(
		ConfigKey(config, k).host,
		ConfigKey(config, k).databaseName,
		ConfigKey(config, k).user,
		ConfigKey(config, k).password,
		ConfigKey(config, k).schema
	  )
	)
  }

  case class ConfigKey(cfg: Config, path: String) {
	def host:String = {
	  cfg.withFallback(s"$path.host", "")
	}
	def databaseName: String = {
	  cfg.withFallback(s"$path.databaseName", "")
	}
	def user: String ={
	  cfg.withFallback(s"$path.user", "")
	}
	def password: String ={
	  cfg.withFallback(s"$path.password", "")
	}
	def schema: String ={
	  cfg.withFallback(s"$path.schema", "")
	}
  }

  private implicit class ConfigOps(val c: Config) extends AnyVal {

	def withFallback(path: String, fallback:String) :String = {
	  c.hasPath(path) match {
		case true => c.getString(path)
		case _=> fallback
	  }
	}
  }

}
