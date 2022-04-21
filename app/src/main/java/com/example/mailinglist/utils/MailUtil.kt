package com.example.mailinglist.utils

import com.example.mailinglist.model.MailListItem

class MailUtil {
    companion object {
        fun buildAnswerEmail(originalMail: MailListItem): String {
            val replyToName = getSenderName(originalMail)
            return if (replyToName != null) "Hallo $replyToName,\n\n" else ""

//            var answerMail = "\n\n"
//            answerMail += "-----Ursprüngliche Nachricht-----\n"
//            answerMail =
//                answerMail + "Von: " + if (originalMail.replyTo.personal != null) originalMail.replyTo.personal else "" + "<" + originalMail.replyTo.address + ">\n"
//            answerMail = answerMail + "Gesendet: " + originalMail.sentDate + "\n"
//            answerMail = answerMail + "Betreff: " + originalMail.subject + "\n\n"
//
//            answerMail += if (originalMail.isHtml) HtmlCompat.fromHtml(
//                originalMail.content,
//                HtmlCompat.FROM_HTML_MODE_LEGACY
//            ); else originalMail.content
        }

        fun getSenderName(mail: MailListItem): String? {
            var replyToName: String? = null
            if (mail.replyTo.personal != null) {
                replyToName = mail.replyTo.personal
            } else if (mail.sender.personal != null) {
                replyToName = mail.sender.personal
            }
            return replyToName
        }
    }
}
