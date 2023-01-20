package com.example.getfitness.model

import android.net.Uri

data class Exercise(
    val name: String,
    val image: Uri,
    val observations: String
)