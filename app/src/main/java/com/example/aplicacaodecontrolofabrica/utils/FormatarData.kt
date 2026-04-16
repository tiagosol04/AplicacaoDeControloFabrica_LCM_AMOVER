package com.example.aplicacaodecontrolofabrica.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatarData(data: Date?): String {
    return data?.let {
        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        sdf.format(it)
    } ?: "N/A"
}