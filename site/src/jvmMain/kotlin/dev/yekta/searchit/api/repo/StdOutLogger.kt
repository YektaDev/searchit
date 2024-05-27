package dev.yekta.searchit.api.repo

import com.varabyte.kobweb.api.log.Logger
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs

internal class StdOutLogger(private val logger: Logger) : SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        logger.info("[SQL] ${context.expandArgs(transaction)}")
    }
}
