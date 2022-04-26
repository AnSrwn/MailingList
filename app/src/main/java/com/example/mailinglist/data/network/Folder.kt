package com.example.mailinglist.data.network

import com.sun.mail.imap.IMAPStore
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.search.SearchTerm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.math.ceil

class Folder private constructor(private val jakartaFolder: jakarta.mail.Folder) {
    var messageCount = 0
    var pageCount = 0

    companion object {
        const val SUBJECT_FILTER = "[abelana]"
        const val PAGE_SIZE = 10

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

    suspend fun getAllMessages(
    ): List<Message> {
        return withContext(Dispatchers.IO) {
            getFilteredSortedMessagesOfFolder(jakartaFolder, SUBJECT_FILTER)
        }
    }

    suspend fun getMessages(pageIndex: Int): List<Message> {
        var messages: List<Message>

        withContext(Dispatchers.IO) {
            getPageCount()

            if (pageIndex < 0 || pageIndex > pageCount) {
                throw IndexOutOfBoundsException()
            }

            val messageStartIndex = messageCount - pageIndex * PAGE_SIZE
            var messageEndIndex = messageStartIndex - PAGE_SIZE + 1
            if (messageEndIndex < 1) messageEndIndex = 1

            messages = jakartaFolder
                .getMessages(messageEndIndex, messageStartIndex)
                .toList()
                .filter { message -> message.subject.contains(SUBJECT_FILTER) }
                .sortedByDescending { message -> message.receivedDate }
        }

        return messages
    }

    suspend fun getPageCount(): Int {
        withContext(Dispatchers.IO) {
            messageCount = jakartaFolder.messageCount
        }
        pageCount = ceil((messageCount.toDouble() / PAGE_SIZE.toDouble())).toInt()
        return pageCount
    }

    private suspend fun getFilteredSortedMessagesOfFolder(
        folder: jakarta.mail.Folder,
        subjectFilter: String
    ): List<Message> {
        var messages: List<Message>

        withContext(Dispatchers.IO) {
            val term: SearchTerm = getSearchTerm(subjectFilter)
            messages = folder.search(term).toList()

            coroutineScope {
                messages = messages.sortedByDescending { message -> message.sentDate }
            }
        }

        return messages
    }

    private fun getSearchTerm(subjectFilter: String): SearchTerm {
        val term: SearchTerm = object : SearchTerm() {
            override fun match(message: Message): Boolean {
                try {
                    if (message.subject.contains(subjectFilter)) {
                        return true
                    }
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
                return false
            }
        }
        return term
    }
}
