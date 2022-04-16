package com.example.mailinglist.repository

import androidx.lifecycle.LiveData
import com.example.mailinglist.model.Mail

interface MailService {
    fun getAllMails(): LiveData<Array<Mail>>
}
