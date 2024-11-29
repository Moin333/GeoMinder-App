package com.example.geominderapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.geominderapp.presentation.GeoMinderNavHost
import com.example.geominderapp.ui.theme.GeoMinderAppTheme
import com.google.android.gms.security.ProviderInstaller
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Install updated security provider
        installProvider(this)

        // Request permissions on app launch
        requestPermissionsIfNecessary()

        setContent {
            GeoMinderAppTheme {
                GeoMinderNavHost()
            }
        }
    }

    private fun installProvider(context: Context) {
        try {
            ProviderInstaller.installIfNeeded(context)
            println("Security provider installed successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to install security provider: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermissionsIfNecessary() {
        val permissionsNeeded = mutableListOf<String>()

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            val requestPermissionsLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val allGranted = permissions.values.all { it }
                if (allGranted) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Location permissions are required for geofencing to work.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            requestPermissionsLauncher.launch(permissionsNeeded.toTypedArray())
        }
    }
}
