package com.example.getfitness.model

import com.google.firebase.Timestamp

data class Training(
    val name: Number,
    val description: String,
    val date: Timestamp,
    val author: String = ""
)