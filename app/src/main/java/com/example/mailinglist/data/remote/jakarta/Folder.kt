package com.example.mailinglist.data.remote.jakarta

import com.example.mailinglist.data.model.MailApiModel
import com.example.mailinglist.shared.Constants
import jakarta.mail.Message
import kotlinx.coroutines.*
import kotlin.math.ceil

class Folder constructor(
    private val jakartaFolder: jakarta.mail.Folder,
    private val ioDispatcher: CoroutineDispatcher
) {
    private var messageCount = 0
    private var pageCount = 0

    suspend fun getPageCount(): Int {
        withContext(ioDispatcher) {
            messageCount = jakartaFolder.messageCount
        }
        pageCount = ceil((messageCount.toDouble() / Constants.PAGE_SIZE.toDouble())).toInt()
        return pageCount
    }

    suspend fun getMails(pageIndex: Int): List<MailApiModel> {
        val mails = mutableListOf<Deferred<MailApiModel>>()

        withContext(ioDispatcher) {
            val messages = getPagedMessages(pageIndex)

            for (message in messages) {
                val mail = async {
                    MailApiModel(
                        MessageUtil.getSubjectFromMessage(message),
                        MessageUtil.getContentFromMessage(message) ?: "",
                        message.receivedDate,
                        MessageUtil.getSenderNameFromMessage(message),
                        MessageUtil.getReplyToAddressFromMessage(message),
                        MessageUtil.getImagePartsFromMessage(message)
                    )
                }
                mails.add(mail)
            }
        }

        return mails.awaitAll()
    }

    private suspend fun getPagedMessages(pageIndex: Int): List<Message> {
        var messages: List<Message>
        withContext(ioDispatcher) {
            getPageCount()

            if (pageIndex < 0 || pageIndex > pageCount) {
                throw IndexOutOfBoundsException()
            }

            val messageStartIndex = calculateStartIndex(pageIndex)
            val messageEndIndex = calculateEndIndex(messageStartIndex)
            messages = getFilteredSortedMessages(messageStartIndex, messageEndIndex)
        }

        return messages
    }

    private suspend fun getFilteredSortedMessages(
        messageStartIndex: Int,
        messageEndIndex: Int
    ): List<Message> {
        var messages: List<Message>
        withContext(ioDispatcher) {
            messages = jakartaFolder
                .getMessages(messageEndIndex, messageStartIndex)
                .toList()
                .filter { message -> message.subject.contains(Constants.SUBJECT_FILTER) }
                .sortedByDescending { message -> message.receivedDate }
        }
        return messages
    }

    private fun calculateEndIndex(messageStartIndex: Int): Int {
        var messageEndIndex = messageStartIndex - Constants.PAGE_SIZE + 1
        if (messageEndIndex < 1) messageEndIndex = 1
        return messageEndIndex
    }

    private fun calculateStartIndex(pageIndex: Int): Int {
        return messageCount - pageIndex * Constants.PAGE_SIZE
    }
}
