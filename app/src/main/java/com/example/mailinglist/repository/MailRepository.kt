package com.example.mailinglist.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mailinglist.model.Mail

class MailRepository : MailService {
    override fun getAllMails(): LiveData<Array<Mail>> {
        val mails = MutableLiveData<Array<Mail>>()
        mails.value = emptyArray()

        //TODO get mails via imap and java mail

        //TODO remove mock data
        mails.value = arrayOf(
            Mail(
                "First Mail",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            ),
            Mail(
                "ergsr Mail",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            ),
            Mail(
                "Third Mail",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            ),
            Mail(
                "Whatsup Mail",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            ),
            Mail(
                "Hey ho",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            ),
            Mail(
                "gfrdgvd",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            ),
            Mail(
                "House",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            ),
            Mail(
                "Search",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            ),
            Mail(
                "Test",
                "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."
            )
        )

        return mails
    }

}
