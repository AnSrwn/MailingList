package com.example.mailinglist

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.AnyThread
import net.openid.appauth.*
import org.json.JSONException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock


class AuthStateManager private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val prefsLock: ReentrantLock = ReentrantLock()
    private val currentAuthState: AtomicReference<AuthState> = AtomicReference()

    @get:AnyThread
    val current: AuthState
        get() {
            if (currentAuthState.get() != null) {
                return currentAuthState.get()
            }
            val state = readState()
            return if (currentAuthState.compareAndSet(null, state)) {
                state
            } else {
                currentAuthState.get()
            }
        }

    @AnyThread
    fun replace(state: AuthState): AuthState {
        writeState(state)
        currentAuthState.set(state)
        return state
    }

    @AnyThread
    fun updateAfterAuthorization(
        response: AuthorizationResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = current
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    fun updateAfterTokenResponse(
        response: TokenResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = current
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    fun updateAfterRegistration(
        response: RegistrationResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = current
        if (ex != null) {
            return current
        }
        current.update(response)
        return replace(current)
    }

    @AnyThread
    private fun readState(): AuthState {
        prefsLock.lock()
        return try {
            val currentState = prefs.getString(Constants.AUTH_STATE, null)
                ?: return AuthState()
            try {
                AuthState.jsonDeserialize(currentState)
            } catch (ex: JSONException) {
                Log.w(
                    Constants.TAG_AUTH_STATE_MANAGER,
                    "Failed to deserialize stored auth state - discarding"
                )
                AuthState()
            }
        } finally {
            prefsLock.unlock()
        }
    }

    @AnyThread
    private fun writeState(state: AuthState?) {
        prefsLock.lock()
        try {
            val editor = prefs.edit()
            if (state == null) {
                editor.remove(Constants.AUTH_STATE)
            } else {
                editor.putString(Constants.AUTH_STATE, state.jsonSerializeString())
            }
            check(editor.commit()) { "Failed to write state to shared prefs" }
        } finally {
            prefsLock.unlock()
        }
    }

    companion object {
        private val INSTANCE_REF = AtomicReference(WeakReference<AuthStateManager?>(null))

        @AnyThread
        fun getInstance(context: Context): AuthStateManager {
            var manager = INSTANCE_REF.get().get()
            if (manager == null) {
                manager = AuthStateManager(context.applicationContext)
                INSTANCE_REF.set(WeakReference(manager))
            }
            return manager
        }
    }
}
