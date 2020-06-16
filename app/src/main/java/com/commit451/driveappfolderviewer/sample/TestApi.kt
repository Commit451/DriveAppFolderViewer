package com.commit451.driveappfolderviewer.sample

import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import io.reactivex.rxjava3.core.Completable
import java.util.*

/**
 * Creates testing things
 */
object TestApi {

    private const val MIME_TYPE_FOLDER = "application/vnd.google-apps.folder"
    private const val SPACE = "appDataFolder"

    fun createFiles(drive: Drive): Completable {
        return Completable.defer {
            var folder = File()
                    .setParents(listOf(SPACE))
                    .setMimeType(MIME_TYPE_FOLDER)
                    .setName(UUID.randomUUID().toString())
            folder = drive.files().create(folder)
                    .execute()
            val content = "Hi there"
            // Convert content to an AbstractInputStreamContent instance.
            val contentStream = ByteArrayContent.fromString("text/plain", content)

            var file = File()
                    .setParents(listOf(folder.id))
                    .setMimeType("text/plain")
                    .setName("${System.currentTimeMillis()}-hi")

            file = drive.files().create(file)
                    .execute()

            // Update the metadata and contents.
            drive.files().update(file.id, null, contentStream)
                    .execute()
            Completable.complete()
        }
    }
}
