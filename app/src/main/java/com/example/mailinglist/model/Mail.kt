package com.example.mailinglist.model

import androidx.core.text.HtmlCompat
import com.example.mailinglist.Application
import com.example.mailinglist.Constants
import com.example.mailinglist.StorageManager
import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.Part
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.*


open class Mail(
    val subject: String,
    val content: String,
    val sentDate: Date,
    val senderName: String?,
    val replyToAddress: String,
    val images: List<String>
) {
    companion object {
        suspend operator fun invoke(message: Message): Mail {
            return Mail(
                message.subject,
                getTextFromMessage(message) ?: "",
                message.sentDate,
                getSenderName(message),
                getReplyToAddress(message),
                getImagesFromMessage(message)
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

        private suspend fun getImagesFromMessage(message: Message): List<String> {
            val images = mutableSetOf<Deferred<String>>()

            if (message.isMimeType(Constants.MIME_TYPE_MULTIPART)) {
                val multipart: Multipart = message.content as Multipart

                coroutineScope {
                    for (i in 0 until multipart.count) {
                        val part: MimeBodyPart = multipart.getBodyPart(i) as MimeBodyPart

                        if (Part.ATTACHMENT.equals(
                                part.disposition,
                                true
                            ) && part.isMimeType(Constants.MIME_TYPE_IMAGE_JPEG)
                        ) {
                            val imageName = async {
                                val name: String = createImageName(message, part.fileName)

                                val cacheManager = StorageManager()
                                cacheManager.cacheData(Application.context, part, name)


                                name
                            }

                            images.add(imageName)
                        }
                    }
                }
            }

            return images.awaitAll()
        }

        private fun createImageName(message: Message, fileName: String): String {
            var name: String =
                getReplyToAddress(message) + message.sentDate + fileName
            name = name.replace(" ", "")
            return name
        }

        private fun getTextFromMessage(part: Part): String? {
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
                        if (plainTextResult == null) plainTextResult = getTextFromMessage(bodyPart)
                        continue
                    } else if (bodyPart.isMimeType(Constants.MIME_TYPE_TEXT_HTML)) {
                        val htmlResult = getTextFromMessage(bodyPart)
                        if (htmlResult != null) return htmlResult
                    } else {
                        return getTextFromMessage(bodyPart)
                    }
                }

                return plainTextResult
            } else if (part.isMimeType(Constants.MIME_TYPE_MULTIPART)) {
                val multipart = part.content as Multipart

                for (i in 0 until multipart.count) {
                    val bodyPart: Part = multipart.getBodyPart(i)
                    val result = getTextFromMessage(bodyPart)
                    if (result != null) return result
                }
            }

            return null
        }
    }
}
