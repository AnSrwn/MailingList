package com.example.mailinglist.ui.mailList

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.mailinglist.data.model.Mail
import com.example.mailinglist.data.repository.mail.MailRepository
import com.example.mailinglist.shared.StorageManager
import com.example.mailinglist.shared.notifyObserver
import com.example.mailinglist.ui.model.MailListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MailListViewModel @Inject constructor(
    @com.example.mailinglist.di.MailRepository private val mailRepository: MailRepository,
    @com.example.mailinglist.di.StorageManager private val storageManager: StorageManager
) :
    ViewModel() {
    private var mailListItems = MutableLiveData<MutableList<MailListItem>>(mutableListOf())
    private var pageCount: Int? = null
    private var pageIndex: Int = -1

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    fun getInitialData(): LiveData<MutableList<MailListItem>> {
        return if (mailListItems.value != null && mailListItems.value!!.isNotEmpty()) {
            mailListItems
        } else {
            getNextPage()
        }
    }

    fun getNextPage(): LiveData<MutableList<MailListItem>> {
        return liveData {
            pageIndex += 1

            if (pageCount == null) {
                pageCount = mailRepository.getPageCount()
            }

            if (pageIndex >= pageCount!!) isLastPage = true

            if (isLastPage) {
                emit(mutableListOf())
            } else {
                val mails: List<Mail> = mailRepository.getMails(pageIndex)
                val listItems: MutableList<MailListItem> = convertToMailListItems(mails)

                mailListItems.value?.addAll(listItems)
                mailListItems.notifyObserver()

                emit(listItems)
            }
        }
    }

    private fun convertToMailListItems(mails: List<Mail>): MutableList<MailListItem> {
        val listItems: MutableList<MailListItem> = mails.map { mail ->
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
        }.toMutableList()
        return listItems
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
        val image: ByteArray? = storageManager.retrieveData(imageName)

        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.size)
        }

        return null
    }
}
