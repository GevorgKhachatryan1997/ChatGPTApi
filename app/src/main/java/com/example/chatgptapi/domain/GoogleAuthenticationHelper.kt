package com.example.chatgptapi.domain

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.chatgptapi.model.databaseModels.UserEntity
import com.example.chatgptapi.utils.generateUserId
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class GoogleAuthenticationHelper {

    var onAuthenticationListener: Listener? = null

    fun signInRequest(activity: Activity, googleSignInRequest: (Intent) -> Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        val signInIntent: Intent = googleSignInClient.signInIntent
        googleSignInRequest(signInIntent)
    }

    fun signOutRequest(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
    }

    fun onAuthenticationResult(data: Intent?) {
        try {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = completedTask.getResult(ApiException::class.java)
            val user = UserEntity(generateUserId(), account?.displayName, account?.familyName, account?.email)
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