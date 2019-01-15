package hu.tegzes.tamas.notifier.readers

import com.github.magneticflux.rss.createRssPersister
import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import hu.tegzes.tamas.notifier.notifiers.EmailNotifier
import hu.tegzes.tamas.notifier.notifiers.PushbulletNotifier
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime
import java.net.URL

class RSSReader(val id: EntityID<Int>, val url: URL, titleFilter: String, descFilter: String, lastMatchTime: DateTime) : Reader() {
    private lateinit var root: Rss

    override fun checkForUpdates(): Boolean {
        val persister = createRssPersister()
        val connection = url.openConnection()
        root = persister.read(Rss::class.java, connection.getInputStream())
        for (item in root.channel.items) {
            println(item.title)
        }
        return false
    }

    override fun visitEmailNotifier(email: EmailNotifier) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitPushbulletNotifier(pushbullet: PushbulletNotifier) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}