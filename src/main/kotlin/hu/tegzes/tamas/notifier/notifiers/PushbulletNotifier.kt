package hu.tegzes.tamas.notifier.notifiers

import java.io.FileReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class PushbulletNotifier(val email: String?,
                         val device_iden: String?,
                         val channel_tag: String?,
                         val client_iden: String?) : Notifier {
    override fun visit(visitor: NotifierVisitor) {
        visitor.visitPushbulletNotifier(this)
    }

    fun sendNote(body: String, title: String?) {
        Companion.sendNote(
                title = title,
                body = body,
                email = email,
                device_iden = device_iden,
                channel_tag = channel_tag,
                client_iden = client_iden)
    }

    fun sendLink(url: String, body: String?, title: String?) {
        Companion.sendLink(
                url = url,
                title = title,
                body = body,
                email = email,
                device_iden = device_iden,
                channel_tag = channel_tag,
                client_iden = client_iden)
    }

    companion object {
        private const val PUSHBULLET_API_ADDRESS = """https://api.pushbullet.com/v2/pushes"""
        private val apiKey: String by lazy { FileReader("pushbullet_apikey.txt").readLines()[0] }

        fun sendNote(body: String,
                     email: String? = null,
                     title: String? = null,
                     device_iden: String? = null,
                     channel_tag: String? = null,
                     client_iden: String? = null) {
            val connection = URL(PUSHBULLET_API_ADDRESS).openConnection() as HttpsURLConnection
            connection.doOutput = true
            connection.doInput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.setRequestProperty("Access-Token", apiKey)
            val bufferedWriter = connection.outputStream.writer()
            bufferedWriter.write(
                    """{
            ${if (title != null) ("  \"title\" : \"$title\",\n") else ("")}
            ${if (email != null) ("  \"email\" : \"$email\",\n") else ("")}
            ${if (device_iden != null) ("  \"device_iden\" : \"$device_iden\",\n") else ("")}
            ${if (channel_tag != null) ("  \"channel_tag\" : \"$channel_tag\",\n") else ("")}
            ${if (client_iden != null) ("  \"client_iden\" : \"$client_iden\",\n") else ("")}
            "type" : "note",
            "body" : "$body"
        }""")
            bufferedWriter.close()
            val bufferedReader = connection.inputStream.bufferedReader()
            for (line in bufferedReader.lines()) {
                println(line)
            }
            bufferedReader.close()
        }

        fun sendLink(url: String,
                     title: String? = null,
                     body: String? = null,
                     email: String? = null,
                     device_iden: String? = null,
                     channel_tag: String? = null,
                     client_iden: String? = null) {
            val connection = URL(PUSHBULLET_API_ADDRESS).openConnection() as HttpsURLConnection
            connection.doOutput = true
            connection.doInput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.setRequestProperty("Access-Token", apiKey)
            val bufferedWriter = connection.outputStream.writer()
            bufferedWriter.write(
                    """{
            ${if (title != null) ("  \"title\" : \"$title\",\n") else ("")}
            ${if (body != null) ("  \"body\" : \"$body\",\n") else ("")}
            ${if (email != null) ("  \"email\" : \"$email\",\n") else ("")}
            ${if (device_iden != null) ("  \"device_iden\" : \"$device_iden\",\n") else ("")}
            ${if (channel_tag != null) ("  \"channel_tag\" : \"$channel_tag\",\n") else ("")}
            ${if (client_iden != null) ("  \"client_iden\" : \"$client_iden\",\n") else ("")}
            "url" : "$url",
            "type" : "link"
        }""")
            bufferedWriter.close()
            val bufferedReader = connection.inputStream.bufferedReader()
            for (line in bufferedReader.lines()) {
                println(line)
            }
            bufferedReader.close()
        }
    }

}
