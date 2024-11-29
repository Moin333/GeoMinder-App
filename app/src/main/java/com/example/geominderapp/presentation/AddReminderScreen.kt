package com.example.geominderapp.presentation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.geominderapp.domain.model.Reminder
import androidx.hilt.navigation.compose.hiltViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    navController: NavController,
    viewModel: GeoMinderViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // State variables for input fields and error checks
    var title by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("100") } // Default radius value

    var titleError by remember { mutableStateOf(false) }
    var latitudeError by remember { mutableStateOf(false) }
    var longitudeError by remember { mutableStateOf(false) }
    var radiusError by remember { mutableStateOf(false) }

    val uiMessage by viewModel.uiMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState(initial = false)


    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearUiMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Reminder") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = it.isBlank()
                        },
                        label = { Text("Title") },
                        isError = titleError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (titleError) Text("Title is required", color = MaterialTheme.colorScheme.error)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = { Text("Location Name (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = latitude,
                        onValueChange = {
                            latitude = it
                            latitudeError = latitude.toDoubleOrNull()?.let { it !in -90.0..90.0 } ?: true
                        },
                        label = { Text("Latitude") },
                        isError = latitudeError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (latitudeError) Text("Latitude must be between -90 and 90", color = MaterialTheme.colorScheme.error)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = longitude,
                        onValueChange = {
                            longitude = it
                            longitudeError = longitude.toDoubleOrNull()?.let { it !in -180.0..180.0 } ?: true
                        },
                        label = { Text("Longitude") },
                        isError = longitudeError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (longitudeError) Text("Longitude must be between -180 and 180", color = MaterialTheme.colorScheme.error)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = radius,
                        onValueChange = {
                            radius = it
                            radiusError = radius.toFloatOrNull()?.let { it <= 0 } ?: true
                        },
                        label = { Text("Radius (in meters)") },
                        isError = radiusError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (radiusError) Text("Radius must be greater than 0", color = MaterialTheme.colorScheme.error)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            titleError = title.isBlank()
                            latitudeError = latitude.toDoubleOrNull()?.let { it !in -90.0..90.0 } ?: true
                            longitudeError = longitude.toDoubleOrNull()?.let { it !in -180.0..180.0 } ?: true
                            radiusError = radius.toFloatOrNull()?.let { it <= 0 } ?: true

                            if (!titleError && !latitudeError && !longitudeError && !radiusError) {
                                val reminder = Reminder(
                                    title = title,
                                    locationName = locationName,
                                    latitude = latitude.toDouble(),
                                    longitude = longitude.toDouble(),
                                    radius = radius.toFloat()
                                )
                                println("Reminder added: $reminder")
                                viewModel.addReminder(reminder)
                                println("Reminder added Successfully")
                                navController.navigateUp()
                            } else {
                                Toast.makeText(context, "Please fix the errors above.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Reminder")
                    }
                }
            }
        }
    }
}
