package hu.tegzes.tamas.notifier

import hu.tegzes.tamas.notifier.notifiers.EmailNotifier

fun main(args: Array<String>) {
//    println("Hello")
//    val rssReader = RSSReader(URL("https://www.mavcsoport.hu/mavinform/rss.xml"))
//    val bufferedReader = File("pushbullet_key.txt").bufferedReader()
//    val apiKey = bufferedReader.readLine()
//    bufferedReader.close()
//    val text = rssReader.update()
//    if (text != null) {
//        PushbulletNotifier(apiKey).sendNote(body = text, email = "tamastom96@gmail.com")
//    }
//    EmailNotifier("asdf").asdf();
    EmailNotifier.sendEmail("tegzes.tamas@gmail.com", "árvíztűrő tükörfúrógép", "asdf, árvíztűrő tükörfúrógép")

}