package hu.tegzes.tamas.notifier.notifiers

interface Notifier {
    fun visit(visitor: NotifierVisitor)
}