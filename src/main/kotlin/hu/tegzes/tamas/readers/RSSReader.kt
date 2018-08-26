package hu.tegzes.tamas.readers

import com.github.magneticflux.rss.createRssPersister
import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import java.net.URL

class RSSReader(val url: URL) {
    fun update(): String? {
        val persister = createRssPersister()
        val connection = url.openConnection()
        val root = persister.read(Rss::class.java, connection.getInputStream())
        for (item in root.channel.items) {
            println(item.title)
        }
        return root.channel.items.first().title
    }
}