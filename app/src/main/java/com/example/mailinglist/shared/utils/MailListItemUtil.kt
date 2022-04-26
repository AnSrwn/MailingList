package com.example.mailinglist.shared.utils

import com.example.mailinglist.domain.model.MailListItem

class MailListItemUtil {
    companion object {
        fun buildAnswerEmail(originalMail: MailListItem): String {
            val replyToName = originalMail.senderName
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
    }
}
