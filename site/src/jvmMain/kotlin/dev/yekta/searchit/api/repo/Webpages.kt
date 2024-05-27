package dev.yekta.searchit.api.repo

import org.jetbrains.exposed.dao.id.IntIdTable

private const val SESSION_ID_MAX_LEN = 100
private const val SESSION_ID_KEY = "sessionId"

object Webpages : IntIdTable() {
    val sessionId = varchar(SESSION_ID_KEY, SESSION_ID_MAX_LEN)
    val atEpochSeconds = long("atEpochSeconds")
    val url = text("url")
    val html = text("html")
}
