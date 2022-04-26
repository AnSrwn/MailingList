package com.example.mailinglist.ui.mailList

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.example.mailinglist.Application
import com.example.mailinglist.data.model.Mail
import com.example.mailinglist.data.repository.MailRepository
import com.example.mailinglist.domain.model.MailListItem
import com.example.mailinglist.shared.StorageManager
import com.example.mailinglist.shared.notifyObserver
import kotlinx.coroutines.launch


class MailListViewModel : ViewModel() {
    private var mailListItems = MutableLiveData<MutableList<MailListItem>>()
    private var pageCount: Int? = null
    private var pageIndex: Int = -1
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    fun getNextPage(): LiveData<MutableList<MailListItem>> {
        return liveData {
            val mailRepository = MailRepository()

            if (pageCount == null) {
                pageCount = mailRepository.getPageCount()
            }

            if (pageIndex >= pageCount!!) {
                isLastPage = true
                emit(mutableListOf())
            } else {

                pageIndex += 1

                val mails: List<Mail> = mailRepository.getMails(pageIndex)
                emit(mails.map { mail ->
                    val images = retrieveImages(mail)

                    MailListItem(
                        mail.subject,
                        mail.content,
                        mail.receivedDate,
                        mail.senderName,
                        mail.replyToAddress,
                        images,
                        false
                    )
                }.toMutableList())
            }
        }
    }

    fun getAllMailListItems(): MutableLiveData<MutableList<MailListItem>> {
        viewModelScope.launch {
            val mailRepository = MailRepository()
            val mails: List<Mail> = mailRepository.getAllMails()
            for (mail in mails) {
                val images = retrieveImages(mail)

                val mailListItem = MailListItem(
                    mail.subject,
                    mail.content,
                    mail.receivedDate,
                    mail.senderName,
                    mail.replyToAddress,
                    images,
                    false
                )

                addMailListItem(mailListItem)
            }
        }

        return mailListItems
    }

    private fun addMailListItem(mailListItem: MailListItem) {
        mailListItems.value?.add(mailListItem)
        mailListItems.notifyObserver()
    }

    private fun retrieveImages(mail: Mail): MutableList<Bitmap> {
        val images = mutableListOf<Bitmap>()

        for (imageName in mail.imageNames) {
            val bitmap = retrieveImage(imageName)
            if (bitmap != null) images.add(bitmap)
        }
        return images
    }

    private fun retrieveImage(imageName: String): Bitmap? {
        val cacheManager = StorageManager()
        val image: ByteArray? = cacheManager.retrieveData(Application.context, imageName)

        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.size)
        }

        return null
    }
}
