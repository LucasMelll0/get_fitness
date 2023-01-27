package com.example.getfitness.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar


fun Fragment.goToBack() {
    findNavController().popBackStack()
}

fun Fragment.goTo(destination: Int) {
    findNavController().navigate(destination)
}

fun Fragment.goTo(action: NavDirections) {
    findNavController().navigate(action)
}

fun Fragment.showSnackBar(message: String) {
    view?.let {
        Snackbar.make(
            it,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}