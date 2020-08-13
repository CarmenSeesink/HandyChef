package com.example.handychef.data

import com.google.firebase.Timestamp

data class RecipePost (
    var id: String = "",
    val userId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val headerImageUrl: String = "",
    val title: String = "",
    val body: String = "",
    val steps: String = ""
)