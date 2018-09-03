package hu.tegzes.tamas.notifier.notifiers

interface NotifierVisitor {
    fun visitPushbulletNotifier(pushbullet: PushbulletNotifier)
    fun visitEmailNotifier(email: EmailNotifier)
}