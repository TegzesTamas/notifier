package hu.tegzes.tamas.notifier.readers

import hu.tegzes.tamas.notifier.notifiers.Notifier
import hu.tegzes.tamas.notifier.notifiers.NotifierVisitor

abstract class Reader(private val notifiers: List<Notifier>) : NotifierVisitor {
    fun update() {
        if (checkForUpdates()) {
            for (notifier in notifiers) {
                notifier.visit(this)
            }
        }
    }

    abstract fun checkForUpdates(): Boolean
}