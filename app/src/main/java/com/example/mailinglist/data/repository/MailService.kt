package com.example.mailinglist.data.repository

import com.example.mailinglist.data.model.Mail

interface MailService {
    suspend fun getAllMails(): List<Mail>
}
