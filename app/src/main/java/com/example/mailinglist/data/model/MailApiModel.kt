package com.example.mailinglist.data.model

import jakarta.mail.Part
import java.util.*

data class MailApiModel(
    val subject: String,
    val content: String,
    val receivedDate: Date,
    val senderName: String?,
    val replyToAddress: String,
    val images: List<Part>
) {}
