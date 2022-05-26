package com.commit451.driveappfolderviewer.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.commit451.driveappfolderviewer.DriveAppFolderViewer
import com.commit451.driveappfolderviewer.DriveAppViewerBaseActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * I am cheating by extending this base class, you should not actually do this
 */
class MainActivity : DriveAppViewerBaseActivity() {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        val context = this
        setContent {
            Theme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("DriveAppFolderViewer Sample")
                            },
                        )
                    },
                    content = {
                        Column(modifier = Modifier.padding(all = 16.dp)) {
                            Button(onClick = { createFiles() }) {
                                Text("Crete files")
                            }
                            Button(onClick = {
                                val intent = DriveAppFolderViewer.intent(context)
                                startActivity(intent)
                            }) {
                                Text("Show files")
                            }
                        }
                    }
                )
            }
        }

        viewModel.toastMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT)
                .show()
            viewModel.onToastShown()
        }
    }

    private fun createFiles() {
        viewModel.createFiles(drive!!)
    }

    override fun onSignedIn(googleSignInAccount: GoogleSignInAccount) {
        super.onSignedIn(googleSignInAccount)
        //TODO show progress then remove it
    }
}
