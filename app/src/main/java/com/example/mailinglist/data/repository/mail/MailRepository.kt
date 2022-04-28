package com.example.mailinglist.data.repository.mail

import com.example.mailinglist.data.model.Mail

interface MailRepository {
    suspend fun getPageCount(): Int
    suspend fun getMails(pageIndex: Int): List<Mail>
}
