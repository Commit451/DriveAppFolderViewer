package com.commit451.driveappfolderviewer.sample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var _toastMessage = MutableLiveData<String>(null)

    val toastMessage: LiveData<String> = _toastMessage

    fun createFiles(drive: Drive) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    TestApi.createFiles(drive)
                    sendToast("Files created")
                } catch (e: Exception) {
                    sendToast("Failed to create files. Check LogCat")
                }
            }
        }
    }

    private suspend fun sendToast(message: String) {
        withContext(Dispatchers.Main) {
            _toastMessage.value = message
        }
    }

    fun onToastShown() {
        _toastMessage.value = null
    }
}