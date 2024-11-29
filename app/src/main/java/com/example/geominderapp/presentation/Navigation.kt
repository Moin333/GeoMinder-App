package com.example.geominderapp.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun GeoMinderNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "reminder_list") {

        // Reminder List Screen
        composable(route = "reminder_list") {
            ReminderScreen(
                navController = navController,
            )
        }

        // Add Reminder Screen
        composable(route = "add_reminder") {
            AddReminderScreen(navController = navController)
        }

    }
}

