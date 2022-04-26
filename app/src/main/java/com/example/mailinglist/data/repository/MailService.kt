package com.example.mailinglist.data.repository

import com.example.mailinglist.data.model.Mail

interface MailService {
    suspend fun getPageCount(): Int
    suspend fun getMails(pageIndex: Int): List<Mail>
    suspend fun getAllMails(): List<Mail>
}
