package com.example.mailinglist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mailinglist.model.Mail
import com.example.mailinglist.repository.MailRepository

class MailListViewModel : ViewModel() {
    private val mailRepository = MailRepository()
    private val mails = mailRepository.getAllMails()

    fun getMails(): LiveData<Array<Mail>> {
        return mails
    }
}
