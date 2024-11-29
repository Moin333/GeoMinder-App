package com.example.geominderapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.geominderapp.service.LocationService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startForegroundService(Intent(context, LocationService::class.java)) // For Android O+ versions
        }
    }
}