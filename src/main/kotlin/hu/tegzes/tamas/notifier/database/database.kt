package hu.tegzes.tamas.notifier.database

import hu.tegzes.tamas.notifier.notifiers.EmailNotifier
import hu.tegzes.tamas.notifier.notifiers.Notifier
import hu.tegzes.tamas.notifier.notifiers.PushbulletNotifier
import hu.tegzes.tamas.notifier.readers.RSSReader
import hu.tegzes.tamas.notifier.readers.Reader
import hu.tegzes.tamas.notifier.readers.WebReader
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.SQLiteConfig
import java.net.URL
import java.sql.Connection
import java.sql.DriverManager

fun init(): Pair<MutableMap<EntityID<Int>, Reader>, MutableMap<EntityID<Int>, Notifier>> {
    Database.connect(getNewConnection = {
        val config = SQLiteConfig()
        config.enforceForeignKeys(true)
        DriverManager.getConnection("jdbc:sqlite:/home/tamas/SourceCodes/notifier/db/notifier.db", config.toProperties())
    })

    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    val readers = mutableMapOf<EntityID<Int>, Reader>()
    val notifiers = mutableMapOf<EntityID<Int>, Notifier>()
    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.drop(ReaderTable, NotifierTable, SubscriptionTable, RssReaderTable, WebReaderTable, EmailNotifierTable, PushbulletNotifierTable)
        SchemaUtils.create(ReaderTable, NotifierTable, SubscriptionTable, RssReaderTable, WebReaderTable, EmailNotifierTable, PushbulletNotifierTable)
        RssReaderTable.selectAll().forEach {
            readers[it[RssReaderTable.readerId]] =
                    RSSReader(it[RssReaderTable.readerId],
                            URL(it[RssReaderTable.url]),
                            it[RssReaderTable.titleFilter],
                            it[RssReaderTable.descFilter],
                            it[RssReaderTable.lastMatchTime])
        }
        WebReaderTable.selectAll().forEach {
            readers[it[WebReaderTable.readerId]] =
                    WebReader(it[WebReaderTable.readerId],
                            URL(it[WebReaderTable.url]),
                            it[WebReaderTable.lastContent])
        }
        EmailNotifierTable.selectAll().forEach {
            notifiers[it[EmailNotifierTable.notifierId]] =
                    EmailNotifier(it[EmailNotifierTable.recipient])
        }
        PushbulletNotifierTable.selectAll().forEach {
            notifiers[it[PushbulletNotifierTable.notifierId]] =
                    PushbulletNotifier(it[PushbulletNotifierTable.email],
                            it[PushbulletNotifierTable.device_iden],
                            it[PushbulletNotifierTable.channel_tag],
                            it[PushbulletNotifierTable.client_iden])
        }
        SubscriptionTable.selectAll().forEach {
            readers[it[SubscriptionTable.readerId]]!!.subscribe(notifiers[it[SubscriptionTable.notifierId]]!!)
        }
    }
    return readers to notifiers
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

fun addRssReader(url: String, titleFilter: String?, descFilter: String?): RSSReader? {
    var rssReader: RSSReader? = null
    transaction {
        val id = ReaderTable.insertAndGetId { }
        RssReaderTable.insert {
            it[RssReaderTable.readerId] = id
            it[RssReaderTable.url] = url
            it[RssReaderTable.titleFilter] = titleFilter
            it[RssReaderTable.descFilter] = descFilter
            it[RssReaderTable.lastMatchTime] = null
        }
        rssReader = RSSReader(id, URL(url), titleFilter, descFilter, null)
    }
    return rssReader
}

fun addWebReader(url: String): WebReader? {
    var webReader: WebReader? = null
    transaction {
        val id = ReaderTable.insertAndGetId { }
        WebReaderTable.insert {
            it[readerId] = id
            it[WebReaderTable.url] = url
            it[lastContent] = null
        }
        webReader = WebReader(id, URL(url), null)
    }
    return webReader
}

fun addEmailNotifier(recipient: String): Pair<EntityID<Int>, EmailNotifier>? {
    var ret: Pair<EntityID<Int>, EmailNotifier>? = null
    transaction {
        val id = NotifierTable.insertAndGetId { }
        EmailNotifierTable.insert {
            it[EmailNotifierTable.notifierId] = id
            it[EmailNotifierTable.recipient] = recipient
        }
        ret = id to EmailNotifier(recipient)
    }
    return ret
}