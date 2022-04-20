package com.example.mailinglist.model

import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.Multipart
import jakarta.mail.Part
import jakarta.mail.internet.InternetAddress
import java.io.IOException
import java.util.*


class Mail(
    val subject: String,
    val content: String,
    val isHtml: Boolean,
    val sentDate: Date,
    val sender: InternetAddress
) {
    companion object {
        operator fun invoke(message: Message): Mail {
            val content = getMessageTextContent(message)

            return Mail(
                message.subject,
                content.text ?: "",
                content.isHtml,
                message.sentDate,
                message.from[0] as InternetAddress
            )
        }

        @Throws(MessagingException::class, IOException::class)
        private fun getMessageTextContent(part: Part): MessageTextContent {
            val mailContent = MessageTextContent(null, false)
            if (part.isMimeType("text/*")) {
                mailContent.text = part.content as String
                mailContent.isHtml = part.isMimeType("text/html")
                return mailContent
            }

            if (part.isMimeType("multipart/alternative")) {
                // prefer html text over plain text
                val multipart = part.content as Multipart
                var text: String? = null
                for (i in 0 until multipart.count) {
                    val bodyPart: Part = multipart.getBodyPart(i)
                    if (bodyPart.isMimeType("text/plain")) {
                        if (text == null) text = getMessageTextContent(bodyPart).text
                        continue
                    } else if (bodyPart.isMimeType("text/html")) {
                        val mc = getMessageTextContent(bodyPart)
                        if (mc.text != null) return mc
                    } else {
                        return getMessageTextContent(bodyPart)
                    }
                }
                mailContent.text = text
                return mailContent
            } else if (part.isMimeType("multipart/*")) {
                val multipart = part.content as Multipart
                for (i in 0 until multipart.count) {
                    val mc = getMessageTextContent(multipart.getBodyPart(i))
                    if (mc.text != null) return mc
                }
            }
            return mailContent
        }
    }

    data class MessageTextContent(var text: String?, var isHtml: Boolean)
}
