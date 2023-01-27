package com.example.getfitness.extensions

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.getfitness.R
import com.google.android.material.snackbar.Snackbar


fun Fragment.goToBack() {
    findNavController().popBackStack()
}

fun Fragment.goTo(destination: Int) {
    findNavController().safeNavigate(destination)
}

fun Fragment.goTo(action: NavDirections) {
    findNavController().safeNavigate(action)
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

fun Fragment.showAlertDialog(message: String, onConfirm: () -> Unit = {}) {
    val builder = AlertDialog.Builder(requireContext())
    builder.setMessage(message)
        .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
            onConfirm()
        }
        .setNegativeButton(getString(R.string.common_cancel), null)
    builder.show()
}