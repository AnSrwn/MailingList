package com.example.mailinglist.utils

import androidx.core.text.HtmlCompat
import com.example.mailinglist.Constants
import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.Part
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart

class MessageUtil {
    companion object {
        fun getSubjectFromMessage(message: Message): String {
            return message.subject.replace(
                Regex("\\[\\w+]\\s+"),
                ""
            )
        }

        fun getContentFromMessage(part: Part): String? {
            if (part.isMimeType(Constants.MIME_TYPE_TEXT_PLAIN)) {
                return part.content as String
            } else if (part.isMimeType(Constants.MIME_TYPE_TEXT_HTML)) {
                val partContent = part.content as String
                val text =
                    HtmlCompat.fromHtml(partContent, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                return text
                    .replace(Regex("\n\n+"), "\n")
                    .replace(Regex("<!--.* -->"), "")
                    .trim()
            }

            // Multipart/alternative means that there are multiple parts with the same content,
            // but in different forms
            // e.g. one part as plain text and the other as html
            if (part.isMimeType(Constants.MIME_TYPE_MULTIPART_ALTERNATIVE)) {
                val multipart = part.content as Multipart
                var plainTextResult: String? = null

                for (i in 0 until multipart.count) {
                    val bodyPart: Part = multipart.getBodyPart(i)

                    if (bodyPart.isMimeType(Constants.MIME_TYPE_TEXT_PLAIN)) {
                        if (plainTextResult == null) plainTextResult =
                            getContentFromMessage(bodyPart)
                        continue
                    } else if (bodyPart.isMimeType(Constants.MIME_TYPE_TEXT_HTML)) {
                        val htmlResult = getContentFromMessage(bodyPart)
                        if (htmlResult != null) return htmlResult
                    } else {
                        return getContentFromMessage(bodyPart)
                    }
                }

                return plainTextResult
            } else if (part.isMimeType(Constants.MIME_TYPE_MULTIPART)) {
                val multipart = part.content as Multipart

                for (i in 0 until multipart.count) {
                    val bodyPart: Part = multipart.getBodyPart(i)
                    val result = getContentFromMessage(bodyPart)
                    if (result != null) return result
                }
            }

            return null
        }

        fun getSenderNameFromMessage(message: Message): String? {
            val replyTo = message.replyTo[0] as InternetAddress
            val sender = message.from[0] as InternetAddress

            return when {
                replyTo.personal != null -> replyTo.personal
                sender.personal != null -> sender.personal
                else -> null
            }
        }

        fun getReplyToAddressFromMessage(message: Message): String {
            return (message.replyTo[0] as InternetAddress).address
        }

        fun getImagePartsFromMessage(message: Message): List<Part> {
            val imageParts = mutableListOf<Part>()

            if (message.isMimeType(Constants.MIME_TYPE_MULTIPART)) {
                val multipart: Multipart = message.content as Multipart

                for (i in 0 until multipart.count) {
                    val part: MimeBodyPart = multipart.getBodyPart(i) as MimeBodyPart

                    if (Part.ATTACHMENT.equals(
                            part.disposition,
                            true
                        ) && part.isMimeType(Constants.MIME_TYPE_IMAGE_JPEG)
                    ) {
                        imageParts.add(part)
                    }
                }
            }

            return imageParts
        }

        fun createImageName(message: Message, fileName: String): String {
            var name: String =
                getReplyToAddressFromMessage(message) + message.sentDate + fileName
            name = name.replace(" ", "")
            return name
        }
    }
}
