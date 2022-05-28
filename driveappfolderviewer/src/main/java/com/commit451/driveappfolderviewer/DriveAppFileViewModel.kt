package com.commit451.driveappfolderviewer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.commit451.okyo.Okyo
import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DriveAppFileViewModel(application: Application) : AndroidViewModel(application) {

    private var _uiState = MutableLiveData(
        State()
    )

    val uiState: LiveData<State> = _uiState

    fun onSignedIn(drive: Drive, fileId: String) {
        loadStuff(drive, fileId)
    }

    private fun loadStuff(drive: Drive, fileId: String) {
        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            error = null,
            emptyMessage = "",
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val file = drive.files().get(fileId)
                        .setFields(FILE_FIELDS)
                        .execute()

                    val inputStream = drive.files().get(fileId)
                        .executeMediaAsInputStream()

                    val content = Okyo.readInputStreamAsString(inputStream)

                    updateState(
                        _uiState.value?.copy(
                            title = file.name,
                            subtitle = "Modified ${file.modifiedTime}",
                            fileAsString = content,
                            isLoading = false,
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, ERROR_MESSAGE, e)
                    _uiState.value = _uiState.value?.copy(
                        error = e,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private suspend fun updateState(state: State?) {
        withContext(Dispatchers.Main) {
            _uiState.value = state
        }
    }

    data class State(
        val isLoading: Boolean = false,
        val title: String = "",
        val subtitle: String = "",
        val fileAsString: String = "",
        val error: Throwable? = null,
        val emptyMessage: String = "",
    )
}