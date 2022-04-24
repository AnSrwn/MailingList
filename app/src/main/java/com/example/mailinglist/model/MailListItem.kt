package com.example.mailinglist.model

import java.util.*

class MailListItem(
    subject: String, content: String, sentDate: Date,
    senderName: String?, replyToAddress: String, images: List<String>, var isExpanded: Boolean
) : Mail(
    subject,
    content,
    sentDate,
    senderName,
    replyToAddress,
    images
) {
    companion object {
        operator fun invoke(mail: Mail): MailListItem {
            return MailListItem(
                mail.subject,
                mail.content,
                mail.sentDate,
                mail.senderName,
                mail.replyToAddress,
                mail.images,
                false
            )
        }
    }

}
