package com.example.mailinglist.data.repository

import com.example.mailinglist.Application
import com.example.mailinglist.data.model.Mail
import com.example.mailinglist.data.network.Folder
import com.example.mailinglist.data.network.Store
import com.example.mailinglist.shared.Constants
import com.example.mailinglist.shared.StorageManager
import com.example.mailinglist.shared.utils.MessageUtil
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

    override suspend fun getPageCount(): Int {
        var pageCount = 0
        withContext(Dispatchers.IO) {
            pageCount = inboxFolder.getPageCount()
        }

        return pageCount
    }

    override suspend fun getMails(pageIndex: Int): List<Mail> {
        val mails = mutableListOf<Deferred<Mail>>()

        withContext(Dispatchers.IO) {
            val messages = inboxFolder.getMessages(pageIndex)

            for (message in messages) {
                val mail = async {
                    val imagesParts = MessageUtil.getImagePartsFromMessage(message)
                    val imageNames = storeImageParts(message, imagesParts)

                    Mail(
                        MessageUtil.getSubjectFromMessage(message),
                        MessageUtil.getContentFromMessage(message) ?: "",
                        message.receivedDate,
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
                        message.receivedDate,
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
