package com.commit451.driveappfolderviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Query;

import java.util.ArrayList;

/**
 * Allows you to view your private app drive files and folders
 */
public class DriveAppFolderViewerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, DriveAppFolderViewerActivity.class);
        return intent;
    }

    TextView mPath;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mMessage;

    FilesAdapter mAdapter;

    GoogleApiClient mGoogleApiClient;
    ArrayList<String> mFolderTitles;
    ArrayList<DriveFolder> mFolderPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_app_folder_viewer);
        mFolderPath = new ArrayList<>();
        mFolderTitles = new ArrayList<>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mPath = (TextView) findViewById(android.R.id.text1);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(android.R.id.progress);
        mRecyclerView = (RecyclerView) findViewById(android.R.id.list);
        mMessage = (TextView) findViewById(android.R.id.message);

        mAdapter = new FilesAdapter(new FilesAdapter.Listener() {
            @Override
            public void onDeleteClicked(Metadata metadata) {
                if (metadata.isFolder()) {
                    metadata.getDriveId().asDriveFolder().delete(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            onDeleted(status);
                        }
                    });
                } else {
                    metadata.getDriveId().asDriveFile().delete(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            onDeleted(status);
                        }
                    });
                }
            }

            @Override
            public void onFileClicked(Metadata metadata) {
                if (metadata.getMimeType().equals(DriveFolder.MIME_TYPE)) {
                    DriveFolder folder = metadata.getDriveId().asDriveFolder();
                    mFolderPath.add(folder);
                    mFolderTitles.add(metadata.getTitle());
                    updatePath();
                    loadFilesInFolder(folder);
                } else {
                    Toast.makeText(DriveAppFolderViewerActivity.this, "This is a file", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onSizeClicked(Metadata metadata) {
                Toast.makeText(DriveAppFolderViewerActivity.this, "File size: " + metadata.getFileSize() + " bytes", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mGoogleApiClient.connect();
        updatePath();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mFolderPath.clear();
        mFolderTitles.clear();
        DriveFolder appFolder = Drive.DriveApi.getAppFolder(mGoogleApiClient);
        mFolderPath.add(appFolder);
        mFolderTitles.add("root");
        loadFilesInFolder(appFolder);
        updatePath();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(DriveAppFolderViewerActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private void loadFilesInFolder(DriveFolder folder) {
        mMessage.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(true);
        Query query = new Query.Builder()
                .build();
        folder.queryChildren(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (metadataBufferResult.getStatus().isSuccess()) {
                    setResultsFromBuffer(metadataBufferResult.getMetadataBuffer());
                } else {
                    showError();
                }
            }
        });
    }



    private void setResultsFromBuffer(MetadataBuffer buffer) {
        if (buffer.getCount() == 0) {
            showEmpty();
            mAdapter.clearMetadatas();
        } else {
            ArrayList<Metadata> metadatas = new ArrayList<>();
            for (Metadata metadata : buffer) {
                metadatas.add(metadata.freeze());
            }
            mAdapter.setMetadatas(metadatas);
        }
        buffer.release();
    }

    private void showError() {
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setText("Error");
    }

    private void showEmpty() {
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setText("Empty");
    }

    private void onDeleted(Status status){
        if (status.isSuccess()) {
            Toast.makeText(DriveAppFolderViewerActivity.this, "File deleted", Toast.LENGTH_SHORT).show();
            refresh();
        } else {
            Toast.makeText(DriveAppFolderViewerActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void refresh() {
        DriveFolder folder = mFolderPath.get(mFolderPath.size()-1);
        loadFilesInFolder(folder);
    }

    private void updatePath() {
        String path = "";
        for (String title : mFolderTitles) {
            path = path + title + "/";
        }
        mPath.setText(path);
    }

    @Override
    public void onBackPressed() {
        if (mFolderPath.size() == 1) {
            super.onBackPressed();
        } else {
            mFolderPath.remove(mFolderPath.size()-1);
            DriveFolder folder = mFolderPath.get(mFolderPath.size()-1);
            loadFilesInFolder(folder);
            mFolderTitles.remove(mFolderTitles.size()-1);
            updatePath();
        }
    }
}
