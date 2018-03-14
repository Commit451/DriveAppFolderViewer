package com.commit451.driveappfolderviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.drive.Metadata
import com.google.android.gms.drive.MetadataBuffer
import com.google.android.gms.drive.query.Query

import java.util.ArrayList

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
    private var folderPath = mutableListOf<DriveFolder>()

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
            override fun onDeleteClicked(metadata: Metadata) {
                driveResourceClient.delete(metadata.driveId.asDriveResource())
                        ?.addOnCompleteListener {
                            Toast.makeText(this@DriveAppFolderViewerActivity, "File deleted", Toast.LENGTH_SHORT)
                                    .show()
                            refresh()
                        }
                        ?.addOnFailureListener {
                            Toast.makeText(this@DriveAppFolderViewerActivity, "Failed to delete", Toast.LENGTH_SHORT)
                                    .show()
                        }
            }

            override fun onFileClicked(metadata: Metadata) {
                if (metadata.mimeType == DriveFolder.MIME_TYPE) {
                    val folder = metadata.driveId.asDriveFolder()
                    folderPath.add(folder)
                    folderTitles.add(metadata.title)
                    updatePath()
                    loadFilesInFolder(folder)
                } else {
                    metadata.driveId.asDriveFile()
                    Toast.makeText(this@DriveAppFolderViewerActivity, "This is a file", Toast.LENGTH_SHORT)
                            .show()
                }
            }

            override fun onSizeClicked(metadata: Metadata) {
                Toast.makeText(this@DriveAppFolderViewerActivity, "File size: " + metadata.fileSize + " bytes", Toast.LENGTH_SHORT)
                        .show()
            }
        })
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener { refresh() }

        updatePath()
    }

    override fun onSignedIn() {
        super.onSignedIn()
        folderPath.clear()
        folderTitles.clear()
        driveResourceClient.appFolder
                .addOnCompleteListener {
                    folderPath.add(it.result)
                    folderTitles.add("root")
                    loadFilesInFolder(it.result)
                    updatePath()
                }

    }

    private fun loadFilesInFolder(folder: DriveFolder) {
        textMessage.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = true
        val query = Query.Builder()
                .build()
        driveResourceClient.queryChildren(folder, query)
                .addOnCompleteListener {
                    swipeRefreshLayout.isRefreshing = false
                    setResultsFromBuffer(it.result)
                }
                .addOnFailureListener {
                    swipeRefreshLayout.isRefreshing = false
                    showError()
                }
    }


    private fun setResultsFromBuffer(buffer: MetadataBuffer) {
        if (buffer.count == 0) {
            showEmpty()
            adapter.clearMetadatas()
        } else {
            val metadatas = ArrayList<Metadata>()
            for (metadata in buffer) {
                metadatas.add(metadata.freeze())
            }
            adapter.setMetadatas(metadatas)
        }
        buffer.release()
    }

    private fun showError() {
        textMessage.visibility = View.VISIBLE
        textMessage.text = "Error"
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
