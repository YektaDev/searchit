package dev.yekta.searchit.api.repo

import com.varabyte.kobweb.api.log.Logger
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

private const val DB_PATH = "krawler_data.db"

class DBManager(logger: Logger) {
    private val db = Database.connect("jdbc:sqlite:$DB_PATH", "org.sqlite.JDBC")

    init {
        org.jetbrains.exposed.sql.transactions.transaction {
            addLogger(StdOutLogger(logger))
            SchemaUtils.create(Webpages)
        }
    }

    suspend fun <T> transaction(statement: suspend Transaction.() -> T) =
        newSuspendedTransaction(Dispatchers.IO, db = db, statement = statement)
}
