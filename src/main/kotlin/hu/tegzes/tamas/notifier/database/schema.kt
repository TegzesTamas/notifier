package hu.tegzes.tamas.notifier.database

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

object ReaderTable : IntIdTable("Reader")

object NotifierTable : IntIdTable("Notifier")

object SubscriptionTable : Table("Subscription") {
    val notifierId = SubscriptionTable.entityId("notifierId", NotifierTable) references NotifierTable.id
    val readerId = SubscriptionTable.entityId("readerId", ReaderTable) references ReaderTable.id
}


object RssReaderTable : Table("RssReader") {
    val url = RssReaderTable.varchar("url", 100)
    val titleFilter = RssReaderTable.varchar("titleFilter", 100).nullable()
    val descFilter = RssReaderTable.varchar("descFilter", 100).nullable()
    val lastMatchTime = RssReaderTable.datetime("lastMatchTime").nullable()
    val readerId = (RssReaderTable.entityId("readerId", ReaderTable) references ReaderTable.id).primaryKey()
}

object WebReaderTable : IntIdTable("WebReader") {
    val url = WebReaderTable.varchar("url", 100)
    val lastContent = WebReaderTable.text("lastContent").nullable()
    var readerId = (WebReaderTable.entityId("readerId", ReaderTable) references ReaderTable.id).primaryKey()
}

object EmailNotifierTable : Table("EmailNotifier") {
    val recipient = EmailNotifierTable.varchar("recipient", 254)
    val notifierId = (EmailNotifierTable.entityId("notifierId", NotifierTable) references NotifierTable.id)
            .primaryKey()
}

object PushbulletNotifierTable : Table("PushbulletNotifier") {
    val email = PushbulletNotifierTable.varchar("email", 254)
    val device_iden = PushbulletNotifierTable.varchar("device_iden", 254).nullable()
    val channel_tag = PushbulletNotifierTable.varchar("channel_tag", 254).nullable()
    val client_iden = PushbulletNotifierTable.varchar("client_iden", 254).nullable()
    val notifierId = (PushbulletNotifierTable.entityId("notifierId", NotifierTable) references NotifierTable.id)
            .primaryKey()
}
