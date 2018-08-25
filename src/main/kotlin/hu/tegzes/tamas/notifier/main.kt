package hu.tegzes.tamas.notifier

import hu.tegzes.tamas.notifiers.PushbulletNotifier
import java.io.File

fun main(args: Array<String>) {
    println("Hello")
    val apiKey = File("pushbullet_key.txt").bufferedReader().readLine()
    PushbulletNotifier(apiKey).sendLink(url = "github.com",email = "tamastom96@gmail.com")

}