package com.example.mailinglist.ui.mailList

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.mailinglist.Application
import com.example.mailinglist.data.model.Mail
import com.example.mailinglist.data.repository.MailRepository
import com.example.mailinglist.domain.model.MailListItem
import com.example.mailinglist.shared.StorageManager


class MailListViewModel : ViewModel() {
    private val mailListItems: LiveData<List<MailListItem>> = liveData {
        val mailRepository = MailRepository()
        val mails: List<Mail> = mailRepository.getAllMails()
        val data = mails.map { mail ->
            val images = retrieveImages(mail)

            MailListItem(
                mail.subject,
                mail.content,
                mail.sentDate,
                mail.senderName,
                mail.replyToAddress,
                images,
                false
            )
        }
        emit(data)
    }

    fun getMailListItems(): LiveData<List<MailListItem>> {
        return mailListItems
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
