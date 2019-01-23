package hu.tegzes.tamas.notifier.notifiers

abstract class Notifier {
    abstract fun visit(visitor: NotifierVisitor)
    abstract override fun toString(): String
}