package com.example.mailinglist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.mailinglist.model.Mail
import com.example.mailinglist.repository.MailRepository

class MailListViewModel : ViewModel() {
    private val mailRepository = MailRepository()
    private val mails: LiveData<Array<Mail>> = liveData {
        val data: Array<Mail> = mailRepository.getAllMails()
        emit(data)
    }

    fun getMails(): LiveData<Array<Mail>> {
        return mails
    }
}
