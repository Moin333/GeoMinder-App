package com.example.geominderapp.domain.model

data class Reminder(
    var id: String = "",
    var title: String = "",
    var locationName: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var radius: Float = 0f
) {
    // Explicit no-argument constructor for Firestore
    constructor() : this("", "", "", 0.0, 0.0, 0f)
}
