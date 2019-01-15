package hu.tegzes.tamas.notifier.notifiers

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.MimeMessage


class EmailNotifier(val recipient: String) : Notifier {
    override fun visit(visitor: NotifierVisitor) {
        visitor.visitEmailNotifier(this)
    }

    fun sendEmail(subject: String, body: String) {
        Companion.sendEmail(recipient, subject, body)
    }

    private companion object {
        private const val APPLICATION_NAME = "Notifiers"
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
        private const val TOKENS_DIRECTORY_PATH = "tokens"


        private val service: Gmail

        /**
         * Global instance of the scopes required by this quickstart.
         * If modifying these scopes, delete your previously saved tokens/ folder.
         */
        private val SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND)
        private val CREDENTIALS_FILE_PATH = "/credentials.json"


        init {
            val HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
            try {
                val credential = getCredentials(HttpTransport)
                val builder = Gmail.Builder(HttpTransport, JSON_FACTORY, credential)
                builder.applicationName = APPLICATION_NAME
                service = builder.build()
            } catch (exception: IOException) {
                error("${CREDENTIALS_FILE_PATH} does not exist.")
                exception.printStackTrace()
                throw exception
            }
        }

        /**
         * Creates an authorized Credential object.
         * @param HTTP_TRANSPORT The network HTTP Transport.
         * @return An authorized Credential object.
         * @throws IOException If the credentials.json file cannot be found.
         */
        @Throws(IOException::class)
        private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
            // Load client secrets.
            val inputStream = EmailNotifier::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

            // Build flow and trigger user authorization request.
            val flow = GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(FileDataStoreFactory(java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build()
            return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
        }

        fun sendEmail(recipient: String, subject: String, body: String) {
            val props = Properties()
            val session = Session.getDefaultInstance(props)
            val email = MimeMessage(session)
            email.setRecipients(Message.RecipientType.TO, recipient)
            email.setSubject(subject, "UTF-8")
            email.setText(body, "UTF-8")

            val buffer = ByteArrayOutputStream()
            email.writeTo(buffer)
            val encodedEmail = Base64.getUrlEncoder().encodeToString(buffer.toByteArray())
            val message = com.google.api.services.gmail.model.Message()
            message.setRaw(encodedEmail)

            service.users().messages().send("me", message).execute()
            println("Message ID : ${message.id}")
            println(message.toPrettyString())
        }

    }
}