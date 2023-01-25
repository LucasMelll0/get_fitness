package com.example.getfitness.extensions

import android.net.Uri
import android.widget.ImageView
import coil.load
import com.example.getfitness.R

fun ImageView.tryToLoadImage(
    uri: Uri? = null,
) {
    load(uri) {
        error(R.drawable.ic_error)
        fallback(R.drawable.default_image)
    }
}