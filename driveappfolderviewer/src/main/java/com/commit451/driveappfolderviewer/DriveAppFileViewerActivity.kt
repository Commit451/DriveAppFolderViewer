package com.commit451.driveappfolderviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Shows the contents of a single file within the app folder
 */
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

    private lateinit var viewModel: DriveAppFileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dafv_activity_drive_app_file_viewer)

        viewModel = ViewModelProvider(this)[DriveAppFileViewModel::class.java]

        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.dafv_ic_close_black_24dp)
        DrawableCompat.setTint(
            toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.dafv_navigation_color)
        )
        toolbar.setNavigationOnClickListener { onBackPressed() }
        textMessage = findViewById(android.R.id.text1)
        progress = findViewById(android.R.id.progress)

        viewModel.uiState.observe(this) { state ->
            toolbar.title = state.title
            toolbar.subtitle = state.subtitle
            if (state.error != null) {
                error(state.error)
            }
            if (state.emptyMessage.isNotEmpty()) {
                textMessage.text = state.emptyMessage
            }
            if (state.fileAsString.isNotEmpty()) {
                textMessage.text = state.fileAsString
            }

            progress.isVisible = state.isLoading
        }
    }

    override fun onSignedIn(googleSignInAccount: GoogleSignInAccount) {
        super.onSignedIn(googleSignInAccount)
        val fileId = intent.getStringExtra(KEY_FILE_ID)!!
        viewModel.onSignedIn(drive!!, fileId)
    }

    private fun error(throwable: Throwable) {
        Toast.makeText(this, "Unable to load file contents", Toast.LENGTH_LONG)
            .show()
        finish()
        Log.e(TAG, ERROR_MESSAGE, throwable)
    }
}