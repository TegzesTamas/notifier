package hu.tegzes.tamas.notifier.readers

import hu.tegzes.tamas.notifier.notifiers.Notifier
import hu.tegzes.tamas.notifier.notifiers.NotifierVisitor

abstract class Reader() : NotifierVisitor {
    private val notifiers = mutableSetOf<Notifier>()

    fun subscribe(notifier: Notifier) {
        notifiers.add(notifier)
    }

    fun unsubscribe(notifier: Notifier) {
        notifiers.remove(notifier)
    }

    fun update() {
        if (checkForUpdates()) {
            for (notifier in notifiers) {
                notifier.visit(this)
            }
        }
    }

    abstract fun checkForUpdates(): Boolean
}