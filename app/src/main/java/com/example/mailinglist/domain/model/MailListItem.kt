package com.example.mailinglist.domain.model

import android.graphics.Bitmap
import java.util.*

class MailListItem(
    val subject: String,
    val content: String,
    val sentDate: Date,
    val senderName: String?,
    val replyToAddress: String,
    val images: List<Bitmap>,
    var isExpanded: Boolean
)
