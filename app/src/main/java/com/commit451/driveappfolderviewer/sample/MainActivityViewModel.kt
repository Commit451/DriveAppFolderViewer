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
                    _toastMessage.value = "Files created"
                } catch (e: Exception) {
                    _toastMessage.value = "Failed to create files. Check LogCat"
                }
            }
        }
    }

    fun onToastShown() {
        _toastMessage.value = null
    }
}