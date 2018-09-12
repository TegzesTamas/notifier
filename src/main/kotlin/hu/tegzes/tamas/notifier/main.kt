package hu.tegzes.tamas.notifier

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


fun main(args: Array<String>) {
    Database.connect("jdbc:sqlite:/home/tamas/SourceCodes/notifier/db/notifier.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(RssReaderTable, WebReaderTable, ReaderTypeTable, ReaderTable, NotifierTypeTable, NotifierTable, SubscriptionTable)


    }
}

object RssReaderTable : Table() {
    val url = varchar("url", 100)
    val titleFilter = varchar("titleFilter", 100)
    val descFilter = varchar("descFilter", 100)
    val lastMatchTime = datetime("lastMatchTime")
    val readerId = (entityId("readerId", ReaderTable) references ReaderTable.id).primaryKey()
}

object WebReaderTable : IntIdTable("WebReader") {
    val url = varchar("url", 100)
    val lastContent = text("lastContent")
    var readerId = (entityId("readerId", ReaderTable) references ReaderTable.id).primaryKey()
}

object ReaderTypeTable : IntIdTable("ReaderType") {
    val name = varchar("name", 40)
}

object ReaderTable : IntIdTable("Reader") {
    val type = entityId("type", ReaderTypeTable) references ReaderTypeTable.id
}

object NotifierTypeTable : IntIdTable("NotifierType") {
    val name = varchar("name", 40)
}

object NotifierTable : IntIdTable("Notifier") {
    val type = entityId("type", NotifierTypeTable) references NotifierTypeTable.id

}

object SubscriptionTable : Table("Subscription") {
    val notifierId = entityId("notifierId", NotifierTable) references NotifierTable.id
    val readerId = entityId("readerId", ReaderTable) references ReaderTable.id
}

