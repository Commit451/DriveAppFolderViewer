package com.commit451.driveappfolderviewer.sample;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;

import rx.Observable;
import rx.Subscriber;

/**
 * Creates observables for testing
 */
public class ObservableFactory {

    public static Observable<Void> createABunchOfFoldersObservable(final GoogleApiClient googleApiClient) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    DriveFolder appFolder = Drive.DriveApi.getAppFolder(googleApiClient);

                    MetadataBuffer result = appFolder.listChildren(googleApiClient)
                            .await()
                            .getMetadataBuffer();
                    //Only create a bunch of folders if there are none
                    if (result.getCount() == 0) {
                        for (int i = 0; i < 10; i++) {
                            DriveFolder folder = appFolder.createFolder(googleApiClient,
                                    new MetadataChangeSet.Builder()
                                            .setTitle("Folder " + i)
                                            .build())
                                    .await().getDriveFolder();
                            DriveContents contents = Drive.DriveApi.newDriveContents(googleApiClient)
                                    .await()
                                    .getDriveContents();
                            folder.createFile(googleApiClient,
                                    new MetadataChangeSet.Builder().setTitle("Some file").build(),
                                    contents);
                        }
                    }
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
