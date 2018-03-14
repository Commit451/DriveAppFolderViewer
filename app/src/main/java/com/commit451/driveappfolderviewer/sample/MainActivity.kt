package com.commit451.driveappfolderviewer.sample

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast

import com.commit451.driveappfolderviewer.DriveAppFolderViewerActivity
import com.commit451.driveappfolderviewer.DriveAppViewerBaseActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.Drive
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : DriveAppViewerBaseActivity() {

    companion object {
        private const val REQUEST_RESOLVE_ERROR = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSignedIn() {
        super.onSignedIn()
        TestApi.createThingsIfNeeded(driveResourceClient)
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
