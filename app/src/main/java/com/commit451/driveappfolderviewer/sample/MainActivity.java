package com.commit451.driveappfolderviewer.sample;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.commit451.driveappfolderviewer.DriveAppFolderViewerActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int REQUEST_RESOLVE_ERROR = 123;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createDummyFolders();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, connectionResult.getErrorCode(), 0).show();
            return;
        }
        try {
            connectionResult.startResolutionForResult(MainActivity.this, REQUEST_RESOLVE_ERROR);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_RESOLVE_ERROR:
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    mGoogleApiClient.connect();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to resolve issue", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void createDummyFolders() {
        ObservableFactory.createABunchOfFoldersObservable(mGoogleApiClient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        Intent intent = DriveAppFolderViewerActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }
}
