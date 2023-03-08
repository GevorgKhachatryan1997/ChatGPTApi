package com.example.chatgptapi.domain

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import com.example.chatgptapi.model.databaseModels.UserEntity
import com.example.chatgptapi.utils.generateUserId
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class GoogleAuthenticationHelper {

    companion object {
        private const val clientId = "887145393846-fl0tq6cn8r00iu6r1icqholmarl3n5u3.apps.googleusercontent.com"
    }

    var onAuthenticationListener: Listener? = null

    fun signInRequest(activity: Activity, googleSignInRequest: (IntentSenderRequest) -> Unit) {
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(clientId)
            .build();

        Identity.getSignInClient(activity)
            .getSignInIntent(request)
            .addOnSuccessListener { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.intentSender).build()
                    googleSignInRequest(intentSenderRequest)
                } catch (e: SendIntentException) {
                    Log.e(GoogleAuthenticationHelper::class.simpleName, "Google Sign-in failed")
                }
            }
            .addOnFailureListener { e -> Log.e(GoogleAuthenticationHelper::class.simpleName, "Google Sign-in failed", e) }
    }

    fun signOutRequest(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
    }

    fun onAuthenticationResult(activity: Activity, data: Intent?) {
        try {
            // TODO use add also user account picture
            val credential: SignInCredential = Identity.getSignInClient(activity).getSignInCredentialFromIntent(data)
            val user = UserEntity(generateUserId(), credential.displayName, credential.familyName, "")
            onAuthenticationListener?.onLoginSuccess(user)
        } catch (e: ApiException) {
            onAuthenticationListener?.onLoginFailure()
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d(ContentValues.TAG, "One-tap dialog was closed.")
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d(ContentValues.TAG, "One-tap encountered a network error.")
                }
                else -> {
                    Log.d(ContentValues.TAG, "Couldn't get credential from result." + " (${e.localizedMessage})")
                }
            }
        }
    }
}

interface Listener {
    fun onLoginSuccess(user: UserEntity)
    fun onLoginFailure()
}