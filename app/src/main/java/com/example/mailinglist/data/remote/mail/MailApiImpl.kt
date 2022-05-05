package com.example.mailinglist.data.remote.mail

import com.example.mailinglist.data.model.MailApiModel
import com.example.mailinglist.data.remote.jakarta.Store
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MailApiImpl @Inject constructor(
    private val store: Store,
    private val ioDispatcher: CoroutineDispatcher
) : MailApi {
    override suspend fun fetchPageCount(): Int {
        var pageCount: Int

        withContext(ioDispatcher) {
            pageCount = store.inbox.await().getPageCount()
        }

        return pageCount
    }

    override suspend fun fetchMails(pageIndex: Int): List<MailApiModel> {
        var mails: List<MailApiModel>

        withContext(ioDispatcher) {
            mails = store.inbox.await().getMails(pageIndex)
        }

        return mails
    }
}
