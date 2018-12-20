package com.commit451.driveappfolderviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.commit451.okyo.Okyo
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class DriveAppFileViewerActivity : DriveAppViewerBaseActivity() {

    companion object {

        private const val KEY_FILE_ID = "fileId"

        fun newIntent(context: Context, fileId: String): Intent {
            val intent = Intent(context, DriveAppFileViewerActivity::class.java)
            intent.putExtra(KEY_FILE_ID, fileId)
            return intent
        }
    }

    private lateinit var toolbar: Toolbar
    private lateinit var progress: ProgressBar
    private lateinit var textMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dafv_activity_drive_app_file_viewer)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.dafv_ic_close_black_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        textMessage = findViewById(android.R.id.text1)
        progress = findViewById(android.R.id.progress)
    }

    override fun onSignedIn(googleSignInAccount: GoogleSignInAccount) {
        super.onSignedIn(googleSignInAccount)
        val fileId = intent.getStringExtra(KEY_FILE_ID)

        drive!!.files().get(fileId)
                .asSingle()
                .subscribe({
                    val modified = it.modifiedTime
                    toolbar.title = it.name
                    toolbar.subtitle = "Last Modified $modified"
                }, {
                    error(it)
                })
        drive!!.files().get(fileId)
                .asInputStream()
                .subscribe({
                    progress.visibility = View.GONE
                    val contents = Okyo.readInputStreamAsString(it)
                    textMessage.text = contents
                }, {
                    error(it)
                })
    }

    private fun error(throwable: Throwable) {
        Toast.makeText(this, "Unable to load file contents", Toast.LENGTH_LONG)
                .show()
        finish()
        Log.e("DriveAppViewer", "Error", throwable)
    }
}