package com.example.mailinglist.shared

class Constants {
    companion object {
        const val STORE_TYPE = "imap"
        const val PORT = 993
        const val IMAP_HOST_Lima = "mail.lima-city.de"
        const val SESSION_PROP_SSL_ENABLE = "mail.imap.ssl.enable"
        const val FOLDER_INBOX = "INBOX"
        const val SUBJECT_FILTER = "[abelana]"

        const val PAGE_SIZE = 10
        const val CARD_MAX_LINES = 5

        const val MIME_TYPE_TEXT = "text/*"
        const val MIME_TYPE_TEXT_PLAIN = "text/plain"
        const val MIME_TYPE_TEXT_HTML = "text/html"
        const val MIME_TYPE_MULTIPART = "multipart/*"
        const val MIME_TYPE_MULTIPART_ALTERNATIVE = "multipart/alternative"
        const val MIME_TYPE_IMAGE_JPEG = "image/jpeg"


    }
}
