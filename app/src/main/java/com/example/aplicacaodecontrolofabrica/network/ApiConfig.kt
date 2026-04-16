package com.example.aplicacaodecontrolofabrica.core.network

import com.example.aplicacaodecontrolofabrica.BuildConfig

object ApiConfig {

    private const val FALLBACK_BASE_URL = "http://10.0.2.2:5137/"

    val BASE_URL: String = normalize(
        BuildConfig.API_BASE_URL.takeIf { it.isNotBlank() } ?: FALLBACK_BASE_URL
    )

    private fun normalize(url: String): String {
        val trimmed = url.trim()
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }
}