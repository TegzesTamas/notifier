package hu.tegzes.tamas.notifier.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.SQLiteConfig
import java.sql.Connection
import java.sql.DriverManager

fun init() {
    Database.connect(getNewConnection = {
        val config = SQLiteConfig()
        config.enforceForeignKeys(true)
        DriverManager.getConnection("jdbc:sqlite:/home/tamas/SourceCodes/notifier/db/notifier.db", config.toProperties())
    })

    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(ReaderTable, NotifierTable, SubscriptionTable, RssReaderTable, WebReaderTable, EmailNotifierTable, PushbulletNotifierTable)
    }
}

fun addSubscription(readerId: EntityID<Int>, notifierId: EntityID<Int>): Boolean {
    try {
        transaction {
            addLogger(StdOutSqlLogger)
            SubscriptionTable.insert {
                it[SubscriptionTable.readerId] = readerId
                it[SubscriptionTable.notifierId] = notifierId
            }
        }
        return true
    } catch (exception: ExposedSQLException) {
        exception.printStackTrace()
        return false
    }
}