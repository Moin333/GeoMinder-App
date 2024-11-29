package com.example.geominderapp.data // Changed package

import com.example.geominderapp.domain.repository.ReminderRepository
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideReminderRepository(
        firestore: FirebaseFirestore
    ): ReminderRepository {
        return ReminderRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideGeofencingClient(
        @ApplicationContext context: Context // Use @ApplicationContext to get the application context
    ): GeofencingClient {
        return LocationServices.getGeofencingClient(context)
    }
}