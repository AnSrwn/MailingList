package com.example.mailinglist.repository

import com.example.mailinglist.model.Mail

interface MailService {
    suspend fun getAllMails(): Array<Mail>
}
