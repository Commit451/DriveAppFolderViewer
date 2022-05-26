package com.commit451.driveappfolderviewer

import android.app.Application
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DriveAppFolderViewModel(application: Application) : AndroidViewModel(application) {

    private var path = mutableListOf<Path>()

    private var _uiState = MutableLiveData(State())

    private var _toastMessage = MutableLiveData<String?>(null)

    val uiState: LiveData<State> = _uiState

    val toastMessage: LiveData<String?> = _toastMessage

    fun onSignedIn(drive: Drive) {
        path.clear()
        path.add(Path(SPACE, SPACE))
        updatePath()
        loadFilesInFolder(drive, SPACE)
    }

    fun onBackPressed(drive: Drive): Boolean {
        return if (path.size == 1) {
            false
        } else {
            path.remove(path.last())
            val folder = path.last()
            loadFilesInFolder(drive, folder.id)
            updatePath()
            true
        }
    }

    fun onFileClicked(drive: Drive, file: File) {
        if (file.mimeType == MIME_TYPE_FOLDER) {
            path.add(Path(file.id, file.name))
            updatePath()
            loadFilesInFolder(drive, file.id)
            //TODO load new folder
        } else {
            val intent = DriveAppFileViewerActivity.newIntent(getApplication(), file.id)
            startActivity(getApplication(), intent, null)
        }
    }

    fun onDeleteFile(drive: Drive, file: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    drive.files().delete(file.id).execute()
                } catch (e: Exception) {
                    _toastMessage.value = e.message
                }
            }
        }
    }

    fun onToastShown() {
        _toastMessage.value = null
    }

    private fun loadFilesInFolder(drive: Drive, fileId: String) {
        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            errorMessage = "",
            emptyMessage = "",
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val files = drive.files()
                        .list()
                        .setSpaces(SPACE)
                        .setQ("'$fileId' in parents")
                        .setFields(FILES_FIELDS)
                        .setPageSize(1000)
                        .execute()
                    if (files.isEmpty()) {
                        _uiState.value = _uiState.value?.copy(
                            emptyMessage = "Empty",
                        )
                    } else {
                        _uiState.value = _uiState.value?.copy(
                            files = files.files,
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, ERROR_MESSAGE, e)
                    _uiState.value = _uiState.value?.copy(
                        errorMessage = "Error"
                    )
                }
            }
        }
    }

    private fun updatePath() {
        _uiState.value = _uiState.value?.copy(
            pathText = path.joinToString("/") { it.name }
        )
    }

    data class State(
        val isLoading: Boolean = false,
        val pathText: String = "",
        val errorMessage: String = "",
        val emptyMessage: String = "",
        val files: List<File> = emptyList(),
    )

    data class Path(val id: String, val name: String)
}