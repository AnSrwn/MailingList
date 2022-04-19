package com.example.mailinglist

class Constants {
    companion object {
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
