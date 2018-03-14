package com.commit451.driveappfolderviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.commit451.okyo.Okyo
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.DriveId

class DriveAppFileViewerActivity : DriveAppViewerBaseActivity() {

    companion object {

        private const val KEY_DRIVE_ID = "driveId"

        fun newIntent(context: Context, driveId: DriveId): Intent {
            val intent = Intent(context, DriveAppFileViewerActivity::class.java)
            intent.putExtra(KEY_DRIVE_ID, driveId)
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
        toolbar.title = "File"
        textMessage = findViewById(android.R.id.text1)
        progress = findViewById(android.R.id.progress)
    }

    override fun onSignedIn() {
        super.onSignedIn()
        val driveId = intent.getParcelableExtra<DriveId>(KEY_DRIVE_ID)
        driveResourceClient.openFile(driveId.asDriveFile(), DriveFile.MODE_READ_ONLY)
                .addOnCompleteListener {
                    progress.visibility = View.GONE
                    val contents = Okyo.readInputStreamAsString(it.result.inputStream)
                    textMessage.text = contents
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Unable to load file contents", Toast.LENGTH_SHORT)
                            .show()
                    finish()
                }
    }
}