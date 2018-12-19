package com.commit451.driveappfolderviewer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveResourceClient

abstract class DriveAppViewerBaseActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 0
    }

    protected lateinit var driveResourceClient: DriveResourceClient

    private val googleSignInClient by lazy {
        buildGoogleSignInClient()
    }

    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_APPFOLDER)
                .build()
        return GoogleSignIn.getClient(this, signInOptions)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInClient.silentSignIn()
                .addOnSuccessListener({ _ ->
                    onSignedIn()
                })
                .addOnFailureListener({
                    // Silent sign-in failed, display account selection prompt
                    startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
                })
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                // Called after user is signed in.
                if (resultCode == Activity.RESULT_OK) {
                    onSignedIn()
                } else {
                    Toast.makeText(this, "Unable to connect to Google Drive", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }
    }

    @CallSuper
    protected open fun onSignedIn() {
        driveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
    }
}