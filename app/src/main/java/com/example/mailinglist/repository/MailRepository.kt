package com.example.mailinglist.repository

import com.example.mailinglist.Application
import com.example.mailinglist.Constants
import com.example.mailinglist.StorageManager
import com.example.mailinglist.model.Mail
import com.example.mailinglist.utils.MessageUtil
import jakarta.mail.Message
import jakarta.mail.Part
import kotlinx.coroutines.*


class MailRepository private constructor(private val inboxFolder: Folder) : MailService {
    companion object {
        suspend operator fun invoke(): MailRepository {
            val folder = withContext(Dispatchers.IO) {
                Store().getFolder(Constants.FOLDER_INBOX)
            }

            return MailRepository(folder)
        }
    }

    override suspend fun getAllMails(): List<Mail> {
        val mails = mutableListOf<Deferred<Mail>>()

        withContext(Dispatchers.IO) {
            val messages = inboxFolder.getAllMessages()

            for (message in messages) {
                val mail = async {
                    val imagesParts = MessageUtil.getImagePartsFromMessage(message)
                    val imageNames = storeImageParts(message, imagesParts)

                    Mail(
                        MessageUtil.getSubjectFromMessage(message),
                        MessageUtil.getContentFromMessage(message) ?: "",
                        message.sentDate,
                        MessageUtil.getSenderNameFromMessage(message),
                        MessageUtil.getReplyToAddressFromMessage(message),
                        imageNames
                    )
                }
                mails.add(mail)
            }
        }

        return mails.awaitAll()
    }

    private suspend fun storeImageParts(message: Message, imageParts: List<Part>): List<String> {
        val images = mutableSetOf<Deferred<String>>()

        coroutineScope {
            for (part in imageParts) {
                val imageName = async {
                    val name: String = MessageUtil.createImageName(message, part.fileName)

                    val cacheManager = StorageManager()
                    cacheManager.cacheData(Application.context, part, name)

                    name
                }

                images.add(imageName)
            }
        }

        return images.awaitAll()
    }
}
