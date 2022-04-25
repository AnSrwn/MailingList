package com.example.mailinglist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.mailinglist.model.Mail
import com.example.mailinglist.model.MailListItem
import com.example.mailinglist.repository.MailRepository


class MailListViewModel : ViewModel() {
    private val mailRepository = MailRepository()
    private val mailListItems: LiveData<List<MailListItem>> = liveData {
        val mails: List<Mail> = mailRepository.getAllMails()
        val data = mails.map { mail ->
            MailListItem(mail)
        }
        emit(data)
    }

    fun getMailListItems(): LiveData<List<MailListItem>> {
        return mailListItems
    }
}
