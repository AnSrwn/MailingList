package com.example.mailinglist.data.remote.jakarta

import com.example.mailinglist.shared.Constants
import com.sun.mail.imap.IMAPStore
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

class Store @Inject constructor(
    @Named("IMAPStore") private val mailStore: IMAPStore,
    private val ioDispatcher: CoroutineDispatcher
) {
    val inbox: Deferred<Folder>
        get() = CoroutineScope(ioDispatcher).async {
            getFolder(Constants.FOLDER_INBOX)
        }

    private suspend fun getFolder(folderName: String): Folder {
        var folder: jakarta.mail.Folder

        withContext(ioDispatcher) {
            folder = mailStore.getFolder(folderName)
            folder.open(jakarta.mail.Folder.READ_ONLY)
        }
        return Folder(folder, ioDispatcher)
    }
}
