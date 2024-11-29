package com.example.geominderapp.data

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class LocationService : Service() {
    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate() {
        super.onCreate()
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    fun addGeofence(geofence: Geofence, intent: PendingIntent, onPermissionDenied: () -> Unit) {
        val request = GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, call the provided callback
            onPermissionDenied()
            return
        }

        geofencingClient.addGeofences(request, intent)
            .addOnSuccessListener { println("Geofence added.") }
            .addOnFailureListener { println("Failed to add geofence.") }
    }

    override fun onBind(intent: Intent?) = null
}