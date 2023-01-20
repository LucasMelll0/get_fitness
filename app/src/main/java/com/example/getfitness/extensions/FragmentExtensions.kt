package com.example.getfitness.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController


fun Fragment.goToBack() {
    findNavController().popBackStack()
}

fun Fragment.goTo(destination: Int) {
    findNavController().navigate(destination)
}

fun Fragment.goTo(action: NavDirections) {
    findNavController().navigate(action)
}