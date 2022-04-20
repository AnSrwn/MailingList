package com.example.mailinglist

class Constants {
    companion object {
        const val STORE_TYPE = "imap"
        const val PORT = 993

        const val IMAP_HOST_Google = "imap.gmail.com"
        const val IMAP_HOST_Lima = "mail.lima-city.de"

        const val SESSION_PROP_SSL_ENABLE = "mail.imap.ssl.enable"
        const val SESSION_PROP_AUTH_MECHANISM = "mail.imap.auth.mechanisms"

        const val FOLDER_INBOX = "INBOX"

        const val SHARED_PREFERENCES_NAME = "AUTH_STATE_PREFERENCE"
        const val AUTH_STATE = "AUTH_STATE"

        const val URL_AUTHORIZATION = "https://accounts.google.com/o/oauth2/v2/auth"
        const val URL_TOKEN_EXCHANGE = "https://www.googleapis.com/oauth2/v4/token"
        const val URL_AUTH_REDIRECT = "com.example.mailinglist:/oauth2redirect"

        // Tags
        const val TAG_AUTH_STATE_MANAGER = "AuthStateManager"
        const val TAG_AUTH = "Auth"
    }
}
