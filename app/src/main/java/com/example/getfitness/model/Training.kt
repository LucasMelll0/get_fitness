package com.example.getfitness.model

import java.sql.Timestamp

data class Training(
    val name: String,
    val description: String,
    val date: Timestamp
)