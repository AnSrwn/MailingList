package com.example.mailinglist.data.repository.mail

import android.content.Context
import com.example.mailinglist.data.model.Mail
import com.example.mailinglist.data.model.MailApiModel
import com.example.mailinglist.data.remote.mail.MailRemoteDataSource
import com.example.mailinglist.shared.StorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.mail.Part
import kotlinx.coroutines.*
import javax.inject.Inject


class MailRepositoryImpl @Inject constructor(
    private val mailRemoteDataSource: MailRemoteDataSource,
    @com.example.mailinglist.di.StorageManager private val storageManager: StorageManager,
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) : MailRepository {
    override suspend fun getPageCount(): Int {
        var pageCount: Int
        withContext(ioDispatcher) {
            pageCount = mailRemoteDataSource.fetchPageCount()
        }

        return pageCount
    }

    override suspend fun getMails(pageIndex: Int): List<Mail> {
        val mails = mutableListOf<Deferred<Mail>>()

        withContext(ioDispatcher) {
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

        withContext(ioDispatcher) {
            for (part in mail.images) {
                val imageName = async {
                    val name: String = createImageName(mail, part)
                    storageManager.cacheData(context, part, name)
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
