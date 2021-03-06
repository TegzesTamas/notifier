package hu.tegzes.tamas.notifier.readers

import hu.tegzes.tamas.notifier.database.WebReaderTable
import hu.tegzes.tamas.notifier.notifiers.EmailNotifier
import hu.tegzes.tamas.notifier.notifiers.PushbulletNotifier
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URL

class WebReader(val id: EntityID<Int>, val url: URL, var lastContent: String?) : Reader() {

    private var exception: Exception? = null

    override fun checkForUpdates(): Boolean {
        var content: String?
        try {
            val connection = url.openConnection()
            connection.connectTimeout = 1000;
            connection.connect()
            val bufferedReader = connection.getInputStream().bufferedReader()
            content = bufferedReader.readText()
            bufferedReader.close()
        } catch (e: Exception) {
            exception = e
            return true
        }
        if (content == lastContent) {
            return false
        }

        if (content == null) {
            lastContent = null
            return true
        }
        lastContent = content
        transaction {
            WebReaderTable.update({ WebReaderTable.readerId eq id }) {
                it[lastContent] = content
            }
        }
        return true
    }

    override fun visitPushbulletNotifier(pushbullet: PushbulletNotifier) {
        val except = exception
        if (except != null) {
            pushbullet.sendNote(title = "WebReader exception!", body = "An exception arose while trying to get $url: ${except.message}")
        } else {
            pushbullet.sendLink(url = url.toString(), title = "Site was updated!")
        }
    }

    override fun visitEmailNotifier(email: EmailNotifier) {
        val except = exception
        if (except != null) {
            val stacktrace = StringWriter()
            except.printStackTrace(PrintWriter(stacktrace))
            email.sendEmail("Webreader exception: ${except.message}, URL:$url", body = "An exception arose while trying to get $url:\n $stacktrace")
        } else {
            email.sendEmail("Site updated: $url", body = "There was an update to the site $url\n$lastContent")
        }
    }

    override fun toString(): String = "WebReader\tURL:$url"

}