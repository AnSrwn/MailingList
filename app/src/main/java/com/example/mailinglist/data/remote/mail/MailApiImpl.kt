package com.example.mailinglist.data.remote.mail

import com.example.mailinglist.data.model.MailApiModel
import com.example.mailinglist.data.remote.jakarta.Folder
import com.example.mailinglist.data.remote.jakarta.Store
import com.example.mailinglist.shared.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MailApiImpl private constructor(
    private val inboxFolder: Folder,
    private val ioDispatcher: CoroutineDispatcher
) : MailApi {
    companion object {
        suspend operator fun invoke(): MailApiImpl {
            val folder = withContext(Dispatchers.IO) {
                Store().getFolder(Constants.FOLDER_INBOX)
            }

            return MailApiImpl(folder, Dispatchers.IO)
        }
    }

    override suspend fun fetchPageCount(): Int {
        var pageCount: Int

        withContext(ioDispatcher) {
            pageCount = inboxFolder.getPageCount()
        }

        return pageCount
    }

    override suspend fun fetchMails(pageIndex: Int): List<MailApiModel> {
        var mails: List<MailApiModel>

        withContext(ioDispatcher) {
            mails = inboxFolder.getMails(pageIndex)
        }

        return mails
    }
}
