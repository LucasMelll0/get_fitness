package com.example.getfitness.model

import android.net.Uri

data class Exercise(
    val name: Number,
    var image: Uri? = null,
    val observations: String,
)