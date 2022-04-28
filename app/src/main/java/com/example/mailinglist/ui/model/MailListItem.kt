package com.example.mailinglist.ui.model

import android.graphics.Bitmap
import java.util.*

class MailListItem(
    val subject: String,
    val content: String,
    val receivedDate: Date,
    val senderName: String?,
    val replyToAddress: String,
    val images: List<Bitmap>,
    var isExpanded: Boolean
)
