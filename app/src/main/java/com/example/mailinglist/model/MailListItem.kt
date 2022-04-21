package com.example.mailinglist.model

import jakarta.mail.internet.InternetAddress
import java.util.*

class MailListItem(
    val subject: String,
    val content: String,
    val isHtml: Boolean,
    val sentDate: Date,
    val sender: InternetAddress,
    val replyTo: InternetAddress,
    var isExpanded: Boolean
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
