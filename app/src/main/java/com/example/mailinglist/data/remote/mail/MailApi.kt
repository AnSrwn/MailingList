package com.example.mailinglist.data.remote.mail

import com.example.mailinglist.data.model.MailApiModel

interface MailApi {
    suspend fun fetchPageCount(): Int
    suspend fun fetchMails(pageIndex: Int): List<MailApiModel>
}
