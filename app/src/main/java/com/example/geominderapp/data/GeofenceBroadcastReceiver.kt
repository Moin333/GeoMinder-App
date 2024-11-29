package com.example.geominderapp.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event != null) {
            when (event.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
                    Log.d("GeofenceBroadcast", "Entered Geofence")
                }
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    Log.d("GeofenceBroadcast", "Exited Geofence")
                }
                else -> {
                    Log.w("GeofenceBroadcast", "Unknown geofence transition")
                }
            }
        } else {
            Log.e("GeofenceBroadcast", "Error: GeofencingEvent is null")
        }
    }
}
