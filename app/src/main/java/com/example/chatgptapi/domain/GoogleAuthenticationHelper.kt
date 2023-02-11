package com.example.chatgptapi.domain

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

object GoogleAuthenticationHelper {

    const val REQ_ONE_TAP = 2
    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null
    private var showOneTapUI = true

    fun createSignInRequest(context: Context) {
        oneTapClient = Identity.getSignInClient(context)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("887145393846-fl0tq6cn8r00iu6r1icqholmarl3n5u3.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    fun signInRequest(activity: Activity, requestCode: Int) {
        signInRequest?.let {
            oneTapClient?.beginSignIn(it)
                ?.addOnSuccessListener(activity) { result ->
                    try {
                        startIntentSenderForResult(
                            activity,
                            result.pendingIntent.intentSender, requestCode,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(ContentValues.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                ?.addOnFailureListener(activity) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(ContentValues.TAG, e.localizedMessage)
                }
        }
    }

    fun loadGoogleAuthentication(requestCode: Int, dataIntent: Intent?) {
        when(requestCode){

            REQ_ONE_TAP ->{
                try {
                    val credential = oneTapClient?.getSignInCredentialFromIntent(dataIntent)
                    val idToken = credential?.googleIdToken
                    when {
                        idToken != null -> {
                            Log.d(ContentValues.TAG, "Got ID token.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(ContentValues.TAG, "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(ContentValues.TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(ContentValues.TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(
                                ContentValues.TAG, "Couldn't get credential from result." +
                                        " (${e.localizedMessage})"
                            )
                        }
                    }
                }
            }
        }
    }
}