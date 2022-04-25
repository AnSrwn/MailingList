package com.example.mailinglist.data.network

import com.sun.mail.imap.IMAPStore
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.search.SearchTerm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class Folder private constructor(private val folder: jakarta.mail.Folder) {
    companion object {
        const val SUBJECT_FILTER = "[abelana]"

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
            getFilteredSortedMessagesOfFolder(folder, SUBJECT_FILTER)
        }
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
