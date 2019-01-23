package hu.tegzes.tamas.notifier

import hu.tegzes.tamas.notifier.database.DataAccess


fun main(args: Array<String>) {
    val readers = DataAccess.readers
    readers.values.forEach { it.update() }
}
