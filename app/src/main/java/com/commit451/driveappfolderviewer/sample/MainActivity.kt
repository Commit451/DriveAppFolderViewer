package com.commit451.driveappfolderviewer.sample

import android.os.Bundle
import android.util.Log
import android.view.View

import com.commit451.driveappfolderviewer.DriveAppFolderViewerActivity
import com.commit451.driveappfolderviewer.DriveAppViewerBaseActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

/**
 * I am cheating by extending this base class, you should not actually do this
 */
class MainActivity : DriveAppViewerBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonCreateFiles.setOnClickListener {
            TestApi.createFiles(drive!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.d("LOG", "Files created")
                        Snackbar.make(root, "Files created", Snackbar.LENGTH_SHORT)
                                .show()
                    }, { throwable ->
                        throwable.printStackTrace()
                        Snackbar.make(root, "Files failed", Snackbar.LENGTH_SHORT)
                                .show()
                    })
        }

        buttonShowFiles.setOnClickListener {
            val intent = DriveAppFolderViewerActivity.newIntent(this@MainActivity)
            startActivity(intent)
        }
    }

    override fun onSignedIn(googleSignInAccount: GoogleSignInAccount) {
        super.onSignedIn(googleSignInAccount)
        progress.visibility = View.GONE
        buttonCreateFiles.visibility = View.VISIBLE
        buttonShowFiles.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        finish()
    }
}
