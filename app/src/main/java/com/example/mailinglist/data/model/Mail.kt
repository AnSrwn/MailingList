package com.example.mailinglist.data.model

import java.util.*

open class Mail(
    val subject: String,
    val content: String,
    val sentDate: Date,
    val senderName: String?,
    val replyToAddress: String,
    val imageNames: List<String>
)
