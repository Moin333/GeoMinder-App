package com.example.geominderapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geominderapp.data.GeofenceHelper
import com.example.geominderapp.domain.model.Reminder
import com.example.geominderapp.domain.repository.ReminderRepository
import com.google.android.gms.location.GeofencingClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class GeoMinderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val geofencingClient: GeofencingClient,
    private val geofenceHelper: GeofenceHelper
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadReminders() {
        viewModelScope.launch {
            try {
                reminderRepository.getAllReminders().collect { reminders ->
                    _reminders.value = reminders
                }
            } catch (e: Exception) {
                _uiMessage.value = "Failed to load reminders: ${e.message}"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("Reminder added: $reminder")
                reminderRepository.addReminder(reminder)
                println("Reminder added successfully")
                setGeofence(reminder)
                println("Geofence set successfully")
                loadReminders()
                _uiMessage.value = "Reminder added successfully!"
            } catch (e: Exception) {
                _uiMessage.value = "Failed to add reminder: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            try {
                reminderRepository.deleteReminder(reminderId)
                _uiMessage.value = "Reminder deleted."
                loadReminders()
            } catch (e: Exception) {
                _uiMessage.value = "Failed to delete reminder: ${e.message}"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun setGeofence(reminder: Reminder) {
        if (!hasLocationPermissions()) {
            _uiMessage.value = "Location permissions are required to set geofence."
            return
        }

        try {
            println("Setting geofence for reminder: $reminder")
            val geofence = geofenceHelper.createGeofence(
                reminder.id,
                reminder.latitude,
                reminder.longitude,
                reminder.radius
            )
            println("Geofence created: $geofence")
            val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
            println("Geofencing request created: $geofencingRequest")
            val pendingIntent = geofenceHelper.getGeofencePendingIntent()
            println("Geofence pending intent created: $pendingIntent")
            geofencingClient.addGeofences(geofencingRequest, pendingIntent).await()
            _uiMessage.value = "Geofence set successfully!"
        } catch (e: SecurityException) {
            _uiMessage.value = "Location permissions are missing: ${e.message}"
        } catch (e: Exception) {
            _uiMessage.value = "Failed to set geofence: ${e.message}"
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun hasLocationPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            geofenceHelper.getContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )
        val backgroundLocationPermission = ContextCompat.checkSelfPermission(
            geofenceHelper.getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                backgroundLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }
}
