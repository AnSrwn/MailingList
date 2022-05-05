package com.example.mailinglist.data.remote.mail

import com.example.mailinglist.data.model.MailApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MailRemoteDataSource @Inject constructor(
    @com.example.mailinglist.di.MailApi private val mailApi: MailApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchPageCount(): Int = withContext(ioDispatcher) {
        mailApi.fetchPageCount()
    }

    suspend fun fetchMails(pageIndex: Int): List<MailApiModel> = withContext(ioDispatcher) {
        mailApi.fetchMails(pageIndex)
    }
}
