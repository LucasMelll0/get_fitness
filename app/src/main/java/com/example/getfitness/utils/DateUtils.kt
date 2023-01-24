package com.example.getfitness.utils

import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "dd/MMM/yyyy"
const val DEFAULT_TIMEZONE = "UTC"

fun formatTimestamp(timestamp: Timestamp): String {
    val millis = timestamp.seconds * 1000 + timestamp.nanoseconds /1000000
    return formatMillis(millis)
}

fun getDatePicker(title: String): MaterialDatePicker<Long> {
    val today = MaterialDatePicker.todayInUtcMilliseconds()
    val constraintsBuilder = CalendarConstraints.Builder().setStart(today)
    constraintsBuilder.setValidator(DateValidatorPointForward.now())
    return MaterialDatePicker.Builder.datePicker()
        .setTitleText(title)
        .setSelection(
            MaterialDatePicker.todayInUtcMilliseconds()
        )
        .setCalendarConstraints(constraintsBuilder.build())
        .build()
}

fun formatMillis(millis: Long): String {
    val date = Date(millis)
    val formatter = SimpleDateFormat(
        DATE_FORMAT, Locale.getDefault()
    ).apply {
        timeZone = TimeZone.getTimeZone(DEFAULT_TIMEZONE)
    }
    return formatter.format(date)
}

fun formatMillisToTimestamp(millis: Long): Timestamp {
    val date = Date(millis)
    return Timestamp(date)
}