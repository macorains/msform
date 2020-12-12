package net.macolabo.sform2.helper

import com.typesafe.config.ConfigFactory
import play.api.db.{Database, Databases}

trait SformTestHelper {

  def withSformDatabase[T](block: Database => T) = {
    // val config = ConfigFactory.load()

    Databases.withDatabase(
      driver = "com.mysql.cj.jdbc.Driver",
      url = "jdbc:mysql://192.168.0.10:3306/sform_test?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF8",
      name = "sformdb",
      config = Map(
        "user" -> "sform",
        "password" -> "sform"
      )
    )(block)
  }
}