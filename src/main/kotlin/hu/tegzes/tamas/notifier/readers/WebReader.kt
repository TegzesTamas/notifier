package hu.tegzes.tamas.notifier.readers

import hu.tegzes.tamas.notifier.notifiers.EmailNotifier
import hu.tegzes.tamas.notifier.notifiers.PushbulletNotifier
import org.jetbrains.exposed.dao.EntityID

class WebReader(val id: EntityID<Int>, val url: String, val lastContent: String) : Reader() {
    override fun checkForUpdates(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitPushbulletNotifier(pushbullet: PushbulletNotifier) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitEmailNotifier(email: EmailNotifier) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}