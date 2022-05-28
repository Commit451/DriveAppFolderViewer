package com.commit451.driveappfolderviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.drive.model.File

/**
 * Allows you to view your private app drive files and folders
 */
class DriveAppFolderViewerActivity : DriveAppViewerBaseActivity() {

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, DriveAppFolderViewerActivity::class.java)
        }
    }

    private lateinit var textPath: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var list: RecyclerView
    private lateinit var textMessage: TextView

    private lateinit var adapter: FilesAdapter

    private lateinit var viewModel: DriveAppFolderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dafv_activity_drive_app_folder_viewer)

        viewModel = ViewModelProvider(this)[DriveAppFolderViewModel::class.java]

        textPath = findViewById(android.R.id.text1)
        swipeRefreshLayout = findViewById(android.R.id.progress)
        list = findViewById(android.R.id.list)
        textMessage = findViewById(android.R.id.message)

        textPath.setOnClickListener {
            onBackPressed()
        }
        val activity = this
        adapter = FilesAdapter(object : FilesAdapter.Listener {
            override fun onDeleteClicked(file: File) {
                viewModel.onDeleteFile(drive!!, file)
            }

            override fun onFileClicked(file: File) {
                viewModel.onFileClicked(activity, drive!!, file)
            }

            override fun onSizeClicked(file: File) {
                Toast.makeText(this@DriveAppFolderViewerActivity, "File size: " + file.getSize() + " bytes", Toast.LENGTH_SHORT)
                        .show()
            }
        })
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        viewModel.uiState.observe(this) {
            textPath.text = it.pathText
            adapter.setFiles(it.files)
        }
        viewModel.toastMessage.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT)
                    .show()
                viewModel.onToastShown()
            }
        }
    }

    override fun onSignedIn(googleSignInAccount: GoogleSignInAccount) {
        super.onSignedIn(googleSignInAccount)
        viewModel.onSignedIn(drive!!)
    }

    private fun showError(throwable: Throwable) {
        Log.e(TAG, ERROR_MESSAGE, throwable)
        textMessage.visibility = View.VISIBLE
        textMessage.text = "Error"
    }

    private fun showEmpty() {
        textMessage.visibility = View.VISIBLE
        textMessage.text = "Empty"
    }

    override fun onBackPressed() {
        val handled = viewModel.onBackPressed(drive!!)
        if (!handled) {
            super.onBackPressed()
        }
    }
}
