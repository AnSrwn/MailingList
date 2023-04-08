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
                        MessageParsingUtil.getSubjectFromMessage(message),
                        (MessageParsingUtil.getContentFromMessage(message) ?: "").prettify(),
                        message.receivedDate,
                        MessageParsingUtil.getSenderNameFromMessage(message),
                        MessageParsingUtil.getReplyToAddressFromMessage(message),
                        MessageParsingUtil.getImagePartsFromMessage(message)
                    )
                }
                mails.add(mail)
            }
        }

        return mails.awaitAll()
    }

    @Suppress("RegExpRedundantEscape")
    private fun String.prettify(): String {
        return this
            .replace(Regex("\\s\n"), "\n") // remove whitespaces between line breaks
            .replace(Regex("\n\\s"), "\n") // remove whitespaces between line breaks
            .replace(Regex("\n{3,}"), "\n\n") // reduce empty lines
            .replace(Regex("<!--.* -->"), "") // remove html code
            .replace(Regex("p.*\\{.*\\}"), "") // remove paragraph styling code
            .replace(Regex("\\*{6,}.+"), "") // remove abelana info text
            .trim()
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
