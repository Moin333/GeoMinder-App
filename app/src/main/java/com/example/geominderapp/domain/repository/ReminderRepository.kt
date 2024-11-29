package com.example.geominderapp.domain.repository

import com.example.geominderapp.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


interface ReminderRepository {
    suspend fun getAllReminders(): Flow<List<Reminder>>
    suspend fun addReminder(reminder: Reminder)
    suspend fun deleteReminder(reminderId: String)

}