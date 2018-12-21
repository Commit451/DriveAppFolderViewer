package com.commit451.driveappfolderviewer

import com.google.api.services.drive.DriveScopes

internal const val MIME_TYPE_FOLDER = "application/vnd.google-apps.folder"

/**
 * Can change this to "drive" for testing
 */
internal const val SPACE = "appDataFolder"

/**
 * Can change this to "DriveScopes.DRIVE_FILE" for testing
 */
internal const val SCOPE = DriveScopes.DRIVE_APPDATA

internal const val FILE_FIELDS = "id,name,modifiedTime,size,mimeType"

internal const val FILES_FIELDS = "files($FILE_FIELDS)"

internal const val TAG = "DriveAppFolderViewer"

internal const val ERROR_MESSAGE = "Something went wrong within $TAG"