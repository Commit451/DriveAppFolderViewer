package com.commit451.driveappfolderviewer

import com.google.api.services.drive.DriveScopes

const val MIME_TYPE_FOLDER = "application/vnd.google-apps.folder"

/**
 * Can change this to "drive" for testing
 */
const val SPACE = "appDataFolder"

/**
 * Can change this to "DriveScopes.DRIVE_FILE" for testing
 */
const val SCOPE = DriveScopes.DRIVE_APPDATA