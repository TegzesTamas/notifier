package hu.tegzes.tamas.notifier.readers

import com.github.magneticflux.rss.createRssPersister
import com.github.magneticflux.rss.namespaces.standard.elements.Item
import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import hu.tegzes.tamas.notifier.database.RssReaderTable
import hu.tegzes.tamas.notifier.notifiers.EmailNotifier
import hu.tegzes.tamas.notifier.notifiers.PushbulletNotifier
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import java.net.URL
import java.time.ZonedDateTime

class RSSReader(val id: EntityID<Int>, val url: URL, titleFilter: String?, descFilter: String?, private var localLastMatchTime: DateTime?) : Reader() {
    val titleFilter = Regex(titleFilter ?: ".*")
    val descFilter = Regex(descFilter ?: ".*")
    private lateinit var freshItem: Item

    override fun checkForUpdates(): Boolean {
        val persister = createRssPersister()
        val connection = url.openConnection()
        val root = persister.read(Rss::class.java, connection.getInputStream())
        var foundUpdate = false
        for (item in root.channel.items) {
            val unixEpoch = item.pubDate?.toEpochSecond() ?: ZonedDateTime.now().toEpochSecond()
            if (localLastMatchTime?.isBefore(unixEpoch) != false) {
                if (item.title?.matches(titleFilter) == true
                        || item.description?.matches(descFilter) == true) {
                    localLastMatchTime = DateTime(unixEpoch)
                    this.freshItem = item
                    foundUpdate = true
                }
            }
        }
        if (foundUpdate) {
            transaction {
                RssReaderTable.update({ RssReaderTable.readerId eq id }) { it[lastMatchTime] = localLastMatchTime }
            }
        }
        return foundUpdate
    }

    override fun visitEmailNotifier(email: EmailNotifier) {
        email.sendEmail("New entry in RSS feed: ${freshItem.title ?: "<untitled>"}",
                """
                    Feed URL: $url
                    Item link: ${freshItem.link ?: "<no link>"}
                    Item description: ${freshItem.description ?: "<no description>"}"""".trimIndent())
    }

    override fun visitPushbulletNotifier(pushbullet: PushbulletNotifier) {
        if (freshItem.link != null) {
            pushbullet.sendLink(freshItem.link.toString(), freshItem.description ?: "", freshItem.title ?: "")
        } else {
            pushbullet.sendNote(title = "New entry in RSS feed: ${freshItem.title ?: "<untitled>"}", body =
            """
                Feed URL: $url
                Item link: ${freshItem.link ?: "<no link>"}
                Item description: ${freshItem.description ?: "<no description>"}""".trimIndent())
        }
    }


    override fun toString(): String =
            "RSSReader\tURL: $url\tTitleFilter: $titleFilter\tDescFilter: $descFilter\tLastMatchTime: $localLastMatchTime"
}