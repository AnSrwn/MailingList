package com.example.mailinglist.data.model

import java.util.*

data class Mail(
    val subject: String,
    val content: String,
    val receivedDate: Date,
    val senderName: String?,
    val replyToAddress: String,
    val imageNames: List<String>
)
