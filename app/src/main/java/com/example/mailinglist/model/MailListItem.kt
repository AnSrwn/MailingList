package com.example.mailinglist.model

import android.graphics.Bitmap
import java.util.*

class MailListItem(
    subject: String,
    content: String,
    sentDate: Date,
    senderName: String?,
    replyToAddress: String,
    imageNames: List<String>,
    var images: List<Bitmap>,
    var isExpanded: Boolean
) : Mail(
    subject,
    content,
    sentDate,
    senderName,
    replyToAddress,
    imageNames
)
