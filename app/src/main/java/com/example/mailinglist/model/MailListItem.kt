package com.example.mailinglist.model

import jakarta.mail.internet.InternetAddress
import java.util.*

class MailListItem(
    subject: String, content: String, isHtml: Boolean, sentDate: Date,
    sender: InternetAddress, replyTo: InternetAddress, var isExpanded: Boolean
) : Mail(
    subject, content,
    isHtml,
    sentDate, sender, replyTo
) {
    companion object {
        operator fun invoke(mail: Mail): MailListItem {
            return MailListItem(
                mail.subject,
                mail.content,
                mail.isHtml,
                mail.sentDate,
                mail.sender,
                mail.replyTo,
                false
            )
        }
    }

}
