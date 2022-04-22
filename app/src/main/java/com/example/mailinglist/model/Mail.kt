package com.example.mailinglist.model

import android.text.Spanned
import androidx.core.text.HtmlCompat
import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.Part
import jakarta.mail.internet.InternetAddress
import java.util.*


open class Mail(
    val subject: String,
    val content: String,
    val sentDate: Date,
    val senderName: String?,
    val replyToAddress: String
) {
    companion object {
        operator fun invoke(message: Message): Mail {
            return Mail(
                message.subject,
                getContent(message) ?: "",
                message.sentDate,
                getSenderName(message),
                getReplyToAddress(message)
            )
        }

        private fun getSenderName(message: Message): String? {
            val replyTo = message.replyTo[0] as InternetAddress
            val sender = message.from[0] as InternetAddress

            return when {
                replyTo.personal != null -> replyTo.personal
                sender.personal != null -> sender.personal
                else -> null
            }
        }

        private fun getReplyToAddress(message: Message): String {
            return (message.replyTo[0] as InternetAddress).address
        }

        private fun getContent(part: Part): String? {
            val content = getTextFromMessage(part)

            return if (content.text == null) {
                null
            } else {
                if (content.isHtml) {
                    val text: Spanned =
                        HtmlCompat.fromHtml(content.text!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    val stringText: String = text.toString()
                    stringText.replace(Regex("\n\n+"), "\n").trim()
                } else {
                    content.text
                }
            }
        }

        private fun getTextFromMessage(part: Part): MessageTextContent {
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
                        if (text == null) text = getTextFromMessage(bodyPart).text
                        continue
                    } else if (bodyPart.isMimeType("text/html")) {
                        val mc = getTextFromMessage(bodyPart)
                        if (mc.text != null) return mc
                    } else {
                        return getTextFromMessage(bodyPart)
                    }
                }
                mailContent.text = text
                return mailContent
            } else if (part.isMimeType("multipart/*")) {
                val multipart = part.content as Multipart
                for (i in 0 until multipart.count) {
                    val mc = getTextFromMessage(multipart.getBodyPart(i))
                    if (mc.text != null) return mc
                }
            }

            return mailContent
        }
    }

    data class MessageTextContent(var text: String?, var isHtml: Boolean)
}
