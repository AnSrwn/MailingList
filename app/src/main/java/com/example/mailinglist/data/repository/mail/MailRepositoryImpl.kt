package com.example.mailinglist.data.repository.mail

import com.example.mailinglist.Application
import com.example.mailinglist.data.model.Mail
import com.example.mailinglist.data.model.MailApiModel
import com.example.mailinglist.data.remote.mail.MailApiImpl
import com.example.mailinglist.data.remote.mail.MailRemoteDataSource
import com.example.mailinglist.shared.StorageManager
import jakarta.mail.Part
import kotlinx.coroutines.*


class MailRepositoryImpl private constructor(private val mailRemoteDataSource: MailRemoteDataSource) {
    companion object {
        suspend operator fun invoke(): MailRepositoryImpl {
            val source = withContext(Dispatchers.IO) {
                MailRemoteDataSource(MailApiImpl(), Dispatchers.IO)
            }

            return MailRepositoryImpl(source)
        }
    }

    suspend fun getPageCount(): Int {
        var pageCount: Int
        withContext(Dispatchers.IO) {
            pageCount = mailRemoteDataSource.fetchPageCount()
        }

        return pageCount
    }

    suspend fun getMails(pageIndex: Int): List<Mail> {
        val mails = mutableListOf<Deferred<Mail>>()

        withContext(Dispatchers.IO) {
            val mailApiModels = mailRemoteDataSource.fetchMails(pageIndex)
            for (mailApiModel in mailApiModels) {
                val mail = async {
                    val imageNames = cacheImages(mailApiModel)

                    Mail(
                        mailApiModel.subject,
                        mailApiModel.content,
                        mailApiModel.receivedDate,
                        mailApiModel.senderName,
                        mailApiModel.replyToAddress,
                        imageNames
                    )
                }
                mails.add(mail)
            }
        }

        return mails.awaitAll()
    }

    private suspend fun cacheImages(mail: MailApiModel): List<String> {
        val images = mutableSetOf<Deferred<String>>()

        coroutineScope {
            for (part in mail.images) {
                val imageName = async {
                    val name: String = createImageName(mail, part)

                    val cacheManager = StorageManager()
                    cacheManager.cacheData(Application.context, part, name)

                    name
                }

                images.add(imageName)
            }
        }

        return images.awaitAll()
    }

    private fun createImageName(
        mail: MailApiModel,
        part: Part
    ): String {
        var name: String =
            mail.replyToAddress + mail.receivedDate + part.fileName
        name = name.replace(" ", "")
        return name
    }
}
