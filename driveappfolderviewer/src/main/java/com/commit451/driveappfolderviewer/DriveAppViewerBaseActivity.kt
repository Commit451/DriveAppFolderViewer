package com.commit451.driveappfolderviewer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import java.util.*


abstract class DriveAppViewerBaseActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 0
    }

    protected var drive: Drive? = null

    private val googleSignInClient by lazy {
        buildGoogleSignInClient()
    }

    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(SCOPE))
                .build()
        return GoogleSignIn.getClient(this, signInOptions)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInClient.silentSignIn()
                .addOnSuccessListener {
                    onSignedIn(it)
                }
                .addOnFailureListener {
                    // Silent sign-in failed, display account selection prompt
                    startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
                }
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                // Called after user is signed in.
                if (resultCode == Activity.RESULT_OK && data != null) {
                    handleSignInResult(data)
                } else {
                    onSignedInFailure(null)
                }
            }
        }
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleAccount ->
                    onSignedIn(googleAccount)
                }
                .addOnFailureListener { exception ->
                    onSignedInFailure(exception)
                }
    }

    @CallSuper
    protected open fun onSignedIn(googleSignInAccount: GoogleSignInAccount) {
        // Use the authenticated account to sign in to the Drive service.
        val credential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(SCOPE))
        credential.selectedAccount = googleSignInAccount.account
        drive = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential)
                .setApplicationName("Drive App Viewer")
                .build()
    }

    protected open fun onSignedInFailure(exception: Exception?) {
        Log.e(TAG, ERROR_MESSAGE, exception)
        Toast.makeText(this, "Unable to connect to Google Drive", Toast.LENGTH_SHORT)
                .show()
    }
}