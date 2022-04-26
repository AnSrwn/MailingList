package com.example.mailinglist.data.network

import com.example.mailinglist.shared.Constants
import com.sun.mail.imap.IMAPStore
import jakarta.mail.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil

class Folder private constructor(private val jakartaFolder: jakarta.mail.Folder) {
    private var messageCount = 0
    private var pageCount = 0

    companion object {
        suspend operator fun invoke(store: IMAPStore, folderName: String): Folder {
            val folder = withContext(Dispatchers.IO) {
                getFolder(store, folderName)
            }

            return Folder(folder)
        }

        private suspend fun getFolder(
            mailStore: IMAPStore,
            folderName: String
        ): jakarta.mail.Folder {
            var folder: jakarta.mail.Folder
            withContext(Dispatchers.IO) {
                folder = mailStore.getFolder(folderName)
                folder.open(jakarta.mail.Folder.READ_ONLY)
            }
            return folder
        }

    }

    suspend fun getMessages(pageIndex: Int): List<Message> {
        var messages: List<Message>

        withContext(Dispatchers.IO) {
            getPageCount()

            if (pageIndex < 0 || pageIndex > pageCount) {
                throw IndexOutOfBoundsException()
            }

            val messageStartIndex = calculateStartIndex(pageIndex)
            val messageEndIndex = calculateEndIndex(messageStartIndex)

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

    suspend fun getPageCount(): Int {
        withContext(Dispatchers.IO) {
            messageCount = jakartaFolder.messageCount
        }
        pageCount = ceil((messageCount.toDouble() / Constants.PAGE_SIZE.toDouble())).toInt()
        return pageCount
    }
}
