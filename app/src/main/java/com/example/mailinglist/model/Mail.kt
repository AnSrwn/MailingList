package com.example.mailinglist.model

import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.Part
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import java.util.*


open class Mail(
    val subject: String,
    val content: String,
    val isHtml: Boolean,
    val sentDate: Date,
    val sender: InternetAddress,
    val replyTo: InternetAddress
) {
    companion object {
        operator fun invoke(message: Message): Mail {
            val content = getMessageContent(message)
            getMessageImages(message)

            return Mail(
                message.subject,
                content.text ?: "",
                content.isHtml,
                message.sentDate,
                message.from[0] as InternetAddress,
                message.replyTo[0] as InternetAddress
            )
        }

        private fun getMessageImages(message: Message) {
            if (message.contentType.contains("multipart")) {
                val multiPart: Multipart = message.content as Multipart

                for (i in 0 until multiPart.count) {
                    val part: MimeBodyPart = multiPart.getBodyPart(i) as MimeBodyPart
                    if (Part.ATTACHMENT.equals(part.disposition, true)) {
                        val name = part.fileName
                        val type = part.contentType
                    }
                }
            }
        }

        private fun getMessageContent(part: Part): MessageContent {
            val mailContent = MessageContent(null, false)
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
                        if (text == null) text = getMessageContent(bodyPart).text
                        continue
                    } else if (bodyPart.isMimeType("text/html")) {
                        val mc = getMessageContent(bodyPart)
                        if (mc.text != null) return mc
                    } else {
                        return getMessageContent(bodyPart)
                    }
                }
                mailContent.text = text
                return mailContent
            } else if (part.isMimeType("multipart/*")) {
                val multipart = part.content as Multipart
                for (i in 0 until multipart.count) {
                    val mc = getMessageContent(multipart.getBodyPart(i))
                    if (mc.text != null) return mc
                }
            }
            return mailContent
        }
    }

    data class MessageContent(var text: String?, var isHtml: Boolean)
}
