import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.kostasakrivos.demo.ktor.config.ConfigReader
import me.kostasakrivos.demo.ktor.model.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    var db: Database? = null

    // Sets the database connection and creates all the tables for that microservice
    fun setPersistence() {
        db = Database.connect(getDataSource())
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> queryDb(statement: Transaction.() -> T): T =
        withContext(Dispatchers.IO) {
            transaction(db) { statement() }
        }

    private fun getDataSource(): HikariDataSource {
        val dbConfig = ConfigReader.db()
        val config = HikariConfig()
        config.driverClassName = dbConfig.driverClassName
        config.jdbcUrl = dbConfig.jdbcUrl
        config.maximumPoolSize = dbConfig.maximumPoolSize
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}