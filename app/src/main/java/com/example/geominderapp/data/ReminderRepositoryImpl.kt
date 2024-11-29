package com.example.geominderapp.data

import com.example.geominderapp.domain.model.Reminder
import com.example.geominderapp.domain.repository.ReminderRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReminderRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ReminderRepository {

    override suspend fun addReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            try {
                firestore.collection("reminders").add(reminder).await()
                println("Reminder added successfully: $reminder")
            } catch (e: Exception) {
                println("Error adding reminder: ${e.message}")
            }
        }
    }


    override suspend fun getAllReminders(): Flow<List<Reminder>> = callbackFlow {
        val listenerRegistration = firestore.collection("reminders")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    println("Error fetching reminders: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                val reminders = snapshots?.documents?.mapNotNull { document ->
                    try {
                        document.toObject(Reminder::class.java)
                    } catch (e: Exception) {
                        println("Error deserializing reminder: ${e.message}")
                        null
                    }
                } ?: emptyList()

                trySend(reminders).isSuccess
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun deleteReminder(reminderId: String) {
        withContext(Dispatchers.IO) {
            firestore.collection("reminders").document(reminderId).delete().await()
        }
    }
}
