package com.commit451.driveappfolderviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.drive.model.File
import io.reactivex.disposables.CompositeDisposable

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

    private var path = mutableListOf<Path>()

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dafv_activity_drive_app_folder_viewer)

        textPath = findViewById<View>(android.R.id.text1) as TextView
        swipeRefreshLayout = findViewById<View>(android.R.id.progress) as SwipeRefreshLayout
        list = findViewById<View>(android.R.id.list) as RecyclerView
        textMessage = findViewById<View>(android.R.id.message) as TextView

        textPath.setOnClickListener {
            onBackPressed()
        }
        adapter = FilesAdapter(object : FilesAdapter.Listener {
            override fun onDeleteClicked(file: File) {
                val disposable = drive!!.files().delete(file.id)
                        .asCompletable()
                        .with()
                        .subscribe({
                            Toast.makeText(this@DriveAppFolderViewerActivity, "File deleted", Toast.LENGTH_SHORT)
                                    .show()
                            refresh()
                        }, {
                            Toast.makeText(this@DriveAppFolderViewerActivity, "Failed to delete", Toast.LENGTH_SHORT)
                                    .show()
                        })
                disposables.add(disposable)
            }

            override fun onFileClicked(file: File) {
                if (file.mimeType == MIME_TYPE_FOLDER) {
                    path.add(Path(file.id, file.name))
                    updatePath()
                    loadFilesInFolder(file.id)
                } else {
                    val intent = DriveAppFileViewerActivity.newIntent(this@DriveAppFolderViewerActivity, file.id)
                    startActivity(intent)
                }
            }

            override fun onSizeClicked(file: File) {
                Toast.makeText(this@DriveAppFolderViewerActivity, "File size: " + file.getSize() + " bytes", Toast.LENGTH_SHORT)
                        .show()
            }
        })
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener { refresh() }

        updatePath()
    }

    override fun onSignedIn(googleSignInAccount: GoogleSignInAccount) {
        super.onSignedIn(googleSignInAccount)
        path.clear()
        path.add(Path(SPACE, SPACE))
        updatePath()
        loadFilesInFolder(SPACE)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun loadFilesInFolder(fileId: String) {
        textMessage.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = true
        disposables.add(
                drive!!.files()
                        .list()
                        .setSpaces(SPACE)
                        .setQ("'$fileId' in parents")
                        .setFields(FILES_FIELDS)
                        .setPageSize(1000)
                        .asSingle()
                        .with()
                        .subscribe({
                            swipeRefreshLayout.isRefreshing = false
                            adapter.setFiles(it.files)
                            if (it.files.isNullOrEmpty()) {
                                showEmpty()
                            }
                        }, {
                            swipeRefreshLayout.isRefreshing = false
                            showError(it)
                        })
        )
    }

    private fun showError(throwable: Throwable) {
        textMessage.visibility = View.VISIBLE
        textMessage.text = "Error"
        Log.e("DriveAppViewer", "Error", throwable)
    }

    private fun showEmpty() {
        textMessage.visibility = View.VISIBLE
        textMessage.text = "Empty"
    }

    private fun refresh() {
        val folder = path.last()
        loadFilesInFolder(folder.id)
    }

    private fun updatePath() {
        textPath.text = path.joinToString("/") { it.name }
    }

    override fun onBackPressed() {
        if (path.size == 1) {
            super.onBackPressed()
        } else {
            path.remove(path.last())
            val folder = path.last()
            loadFilesInFolder(folder.id)
            updatePath()
        }
    }

    data class Path(val id: String, val name: String)
}
