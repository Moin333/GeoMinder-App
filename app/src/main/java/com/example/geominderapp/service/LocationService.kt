package com.example.geominderapp.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.geominderapp.R
import com.example.geominderapp.data.GeofenceHelper
import com.example.geominderapp.domain.model.Reminder
import com.example.geominderapp.domain.repository.ReminderRepository
import com.google.android.gms.location.GeofencingClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var geofencingClient: GeofencingClient

    @Inject
    lateinit var geofenceHelper: GeofenceHelper

    @Inject
    lateinit var reminderRepository: ReminderRepository

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        coroutineScope.launch {
            try {
                val reminders = reminderRepository.getAllReminders().first()
                manageGeofences(reminders)
            } catch (e: Exception) {
                Log.e("LocationService", "Error managing geofence", e)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "GeoMinderChannel")
            .setContentTitle("GeoMinder Running")
            .setContentText("Managing location-based reminders.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "GeoMinderChannel",
            "GeoMinder Location Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private suspend fun manageGeofences(reminders: List<Reminder>) {
        geofencingClient.removeGeofences(geofenceHelper.getGeofencePendingIntent()).await()
        reminders.forEach { reminder ->
            val geofence = geofenceHelper.createGeofence(reminder.id, reminder.latitude, reminder.longitude, reminder.radius)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("LocationService", "Location permission not granted")
                return
            }

            geofencingClient.addGeofences(
                geofenceHelper.getGeofencingRequest(geofence),
                geofenceHelper.getGeofencePendingIntent()
            ).addOnSuccessListener {
                Log.d("LocationService", "Geofence added: ${reminder.id}")
            }.addOnFailureListener { e ->
                Log.e("LocationService", "Failed to add geofence: ${reminder.id}", e)
            }
        }
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
}
