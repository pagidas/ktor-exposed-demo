package me.kostasakrivos.demo.ktor.config

import com.typesafe.config.ConfigFactory

data class ServerConfig(val host: String, val port: Int)

data class DbConfig(val driverClassName: String,
                    val jdbcUrl: String,
                    val maximumPoolSize: Int)

object ConfigReader {

    private val config = ConfigFactory.load()

    fun server() =
        ServerConfig(
            host = config.getString("server.host"),
            port = config.getInt("server.port")
        )

    fun db() =
        DbConfig(
            driverClassName = config.getString("db.driver"),
            jdbcUrl = config.getString("db.url"),
            maximumPoolSize = config.getInt("db.poolSize")
        )
}