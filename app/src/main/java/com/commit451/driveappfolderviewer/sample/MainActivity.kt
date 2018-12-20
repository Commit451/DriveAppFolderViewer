package com.commit451.driveappfolderviewer.sample

import android.os.Bundle

import com.commit451.driveappfolderviewer.DriveAppFolderViewerActivity
import com.commit451.driveappfolderviewer.DriveAppViewerBaseActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * I am cheating by extending this base class, you should not actually do this
 */
class MainActivity : DriveAppViewerBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSignedIn(googleSignInAccount: GoogleSignInAccount) {
        super.onSignedIn(googleSignInAccount)
        TestApi.createThingsIfNeeded(drive!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val intent = DriveAppFolderViewerActivity.newIntent(this@MainActivity)
                    startActivity(intent)
                    finish()
                }, {
                    it.printStackTrace()
                })
    }
}
