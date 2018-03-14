package com.commit451.driveappfolderviewer.sample

import com.commit451.okyo.Okyo
import com.commit451.tisk.toSingle
import com.google.android.gms.drive.DriveResourceClient
import com.google.android.gms.drive.MetadataChangeSet

import io.reactivex.Completable

/**
 * Creates testing things
 */
object TestApi {

    fun createThingsIfNeeded(driveResourceClient: DriveResourceClient): Completable {
        return Completable.defer {
            val appFolder = driveResourceClient.appFolder
                    .toSingle()
                    .blockingGet()

            val result = driveResourceClient.listChildren(appFolder)
                    .toSingle()
                    .blockingGet()
            //Only create a bunch of folders if there are none
            if (result.count == 0) {
                for (i in 0..5) {
                    val folder = driveResourceClient.createFolder(appFolder, MetadataChangeSet.Builder()
                            .setTitle("Folder $i")
                            .build())
                            .toSingle()
                            .blockingGet()
                    val contents = driveResourceClient.createContents()
                            .toSingle()
                            .blockingGet()
                    val content = "Hi there"
                    Okyo.writeByteArrayToOutputStream(content.toByteArray(), contents.outputStream)
                    driveResourceClient.createFile(folder, MetadataChangeSet.Builder().setTitle("Some file").build(), contents)
                }
            }
            Completable.complete()
        }
    }
}
