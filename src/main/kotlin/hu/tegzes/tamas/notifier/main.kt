package hu.tegzes.tamas.notifier

import hu.tegzes.tamas.notifier.database.addEmailNotifier
import hu.tegzes.tamas.notifier.database.addRssReader
import hu.tegzes.tamas.notifier.database.addSubscription
import hu.tegzes.tamas.notifier.database.init


fun main(args: Array<String>) {
    val (readers, notifiers) = init()
    val newReader = addRssReader("https://www.mavcsoport.hu/mavinform/rss.xml", null, null)
    val newNotifierPair = addEmailNotifier("tamastom96@gmail.com")
    if (newReader != null && newNotifierPair != null) {
        readers[newReader.id] = newReader
        val (newNotifierId, newNotifier) = newNotifierPair
        notifiers[newNotifierId] = newNotifier
        addSubscription(newReader.id, newNotifierId)
        newReader.subscribe(newNotifier)
        newReader.update()
    }
    for (reader in readers.values) {
        reader.update()
    }
}
