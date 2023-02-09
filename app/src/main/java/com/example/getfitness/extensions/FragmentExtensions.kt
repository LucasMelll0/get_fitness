package com.example.getfitness.extensions

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.getfitness.R
import com.example.getfitness.utils.checkConnection
import com.google.android.material.snackbar.Snackbar


fun Fragment.goToBack() {
    context?.let {
        findNavController().popBackStack()
    }
}

fun Fragment.goTo(destination: Int) {
    context?.let {
        findNavController().safeNavigate(destination)
    }
}

fun Fragment.goTo(action: NavDirections) {
    context?.let {
        findNavController().safeNavigate(action)
    }
}


fun Fragment.showSnackBar(message: String) {
    context?.let {
        view?.let {
            Snackbar.make(
                it,
                message,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}

fun Fragment.showAlertDialog(message: String, onConfirm: () -> Unit = {}) {
    context?.let {
        val builder = AlertDialog.Builder(it)
        builder.setMessage(message)
            .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(getString(R.string.common_cancel), null)
        builder.show()
    }
}

fun Fragment.safeRun(
    onOffline: () -> Unit = {},
    run: () -> Unit
) {
    context?.let {
        val connected = checkConnection(it)
        if (connected) {
            run()
        }else {
            showSnackBar(getString(R.string.common_offline_message))
            onOffline()
        }
    }
}