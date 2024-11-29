package com.example.geominderapp.presentation

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.geominderapp.domain.model.Reminder
import androidx.hilt.navigation.compose.hiltViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController,
    viewModel: GeoMinderViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val context = LocalContext.current

    // Process UI messages from the ViewModel
    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearUiMessage()
        }
    }

    // Load reminders when the screen is displayed
    LaunchedEffect(Unit) {
        viewModel.loadReminders()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("add_reminder")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (reminders.isEmpty()) {
                Text(
                    text = "No reminders available",
                    textAlign = TextAlign.Center
                )
            } else {
                ReminderList(reminders = reminders, onDelete = { reminderId ->
                    viewModel.deleteReminder(reminderId)
                })
            }

            LocationPermissionButton(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun ReminderList(reminders: List<Reminder>, onDelete: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(reminders.size) { index ->
            val reminder = reminders[index]
            ReminderItem(reminder = reminder, onDelete = { onDelete(reminder.id) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Title: ${reminder.title}")
            Text(text = "Location: ${reminder.latitude}, ${reminder.longitude}")
            Text(text = "Radius: ${reminder.radius} meters")

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LocationPermissionButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Toast.makeText(context, "Permissions granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Location permissions are required for geofencing.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Button(
        onClick = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        },
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("Grant Location Permissions")
    }
}