package com.commit451.driveappfolderviewer

import android.content.Context
import android.content.Intent

/**
 * See [https://github.com/Commit451/DriveAppFolderViewer](https://github.com/Commit451/DriveAppFolderViewer)
 */
object DriveAppFolderViewer {

    /**
     * Get the intent to start the drive folder viewer
     */
    fun intent(context: Context): Intent {
        return DriveAppFolderViewerActivity.newIntent(context)
    }
}