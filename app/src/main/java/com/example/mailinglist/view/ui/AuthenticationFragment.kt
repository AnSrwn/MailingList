package com.example.mailinglist.view.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mailinglist.AuthStateManager
import com.example.mailinglist.Constants
import com.example.mailinglist.Credentials
import com.example.mailinglist.R
import net.openid.appauth.*

class AuthenticationFragment : Fragment() {
    private lateinit var authStateManager: AuthStateManager
    private lateinit var authorizationService: AuthorizationService
    private lateinit var authServiceConfig: AuthorizationServiceConfiguration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authStateManager = AuthStateManager.getInstance(this.requireContext())
//        initAuthorizationService()
        authorizationService = AuthorizationService(this.requireContext())

        if (authStateManager.current.isAuthorized) {
            Log.d(Constants.TAG_AUTH, "Done")
            authStateManager.current.performActionWithFreshTokens(authorizationService) { accessToken, _, _ ->
                Log.d("Token", accessToken.toString())
            }
        } else {
            attemptAuthorization()
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authentication, container, false)
    }

    private fun attemptAuthorization() {
        initAuthServiceConfig()

        val authRequest = getAuthorizationRequest()

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleAuthorizationResponse(result.data!!)
                }
            }

        val authIntent = authorizationService.getAuthorizationRequestIntent(authRequest)
        resultLauncher.launch(authIntent)
    }

    private fun handleAuthorizationResponse(intent: Intent) {

        //TODO handle if response or exception is null
        val authorizationResponse: AuthorizationResponse? =
            AuthorizationResponse.fromIntent(intent)
        val authorizationException: AuthorizationException? = AuthorizationException.fromIntent(
            intent
        )

        authStateManager.updateAfterAuthorization(authorizationResponse, authorizationException)

        if (authorizationResponse == null) {
            //authorization failed
            return
        }

        val tokenExchangeRequest = authorizationResponse.createTokenExchangeRequest()

        authorizationService.performTokenRequest(tokenExchangeRequest) { response, exception ->
            if (exception != null) {
                // authorization failed
            } else if (response != null) {
                authStateManager.updateAfterTokenResponse(response, exception)
                Log.d("Token", response.accessToken.toString())
            }
        }
    }

    private fun getAuthorizationRequest(): AuthorizationRequest {
        val builder = AuthorizationRequest.Builder(
            authServiceConfig,
            Credentials.CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(Constants.URL_AUTH_REDIRECT)
        )
        builder.setScopes("profile")

        return builder.build()
    }

//    private fun initAuthorizationService() {
//        val appAuthConfiguration = AppAuthConfiguration.Builder()
//            .setBrowserMatcher(
//                BrowserAllowList(
//                    VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
//                    VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB,
//                    VersionedBrowserMatcher.FIREFOX_CUSTOM_TAB
//                )
//            ).build()
//
//        authorizationService = AuthorizationService(
//            this.requireContext(),
//            appAuthConfiguration)
//    }

    private fun initAuthServiceConfig() {
        authServiceConfig = AuthorizationServiceConfiguration(
            Uri.parse(Constants.URL_AUTHORIZATION),
            Uri.parse(Constants.URL_TOKEN_EXCHANGE)
        )
    }
}
