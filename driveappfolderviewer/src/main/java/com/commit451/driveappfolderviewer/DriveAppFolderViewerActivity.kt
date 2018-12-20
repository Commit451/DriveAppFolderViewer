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
import io.reactivex.schedulers.Schedulers
import java.util.*

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

    private var folderTitles = mutableListOf<String>()
    private var folderPath = mutableListOf<File>()

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dafv_activity_drive_app_folder_viewer)
        folderPath = ArrayList()
        folderTitles = ArrayList()

        textPath = findViewById<View>(android.R.id.text1) as TextView
        swipeRefreshLayout = findViewById<View>(android.R.id.progress) as SwipeRefreshLayout
        list = findViewById<View>(android.R.id.list) as RecyclerView
        textMessage = findViewById<View>(android.R.id.message) as TextView

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
                    folderPath.add(file)
                    folderTitles.add(file.originalFilename)
                    updatePath()
                    loadFilesInFolder(file)
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

    override fun onSignedIn(googleAccount: GoogleSignInAccount) {
        super.onSignedIn(googleAccount)
        folderPath.clear()
        folderTitles.clear()
        disposables.add(
                drive!!.files()
                        .list()
                        .asSingle()
                        .with()
                        .subscribe({
                            adapter.setFiles(it.files)
                            if (it.files.isNullOrEmpty()) {
                                showEmpty()
                            }
                        }, {
                            showError(it)
                        })
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun loadFilesInFolder(file: File) {
        textMessage.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = true
//        val query = Query.Builder()
//                .build()
//        driveResourceClient.queryChildren(folder, query)
//                .addOnCompleteListener {
//                    swipeRefreshLayout.isRefreshing = false
//                    setResultsFromBuffer(it.result!!)
//                }
//                .addOnFailureListener {
//                    swipeRefreshLayout.isRefreshing = false
//                    showError()
//                }
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
        val folder = folderPath[folderPath.size - 1]
        loadFilesInFolder(folder)
    }

    private fun updatePath() {
        var path = ""
        for (title in folderTitles) {
            path = "$path$title/"
        }
        textPath.text = path
    }

    override fun onBackPressed() {
        if (folderPath.size == 1) {
            super.onBackPressed()
        } else {
            folderPath.removeAt(folderPath.size - 1)
            val folder = folderPath[folderPath.size - 1]
            loadFilesInFolder(folder)
            folderTitles.removeAt(folderTitles.size - 1)
            updatePath()
        }
    }
}
