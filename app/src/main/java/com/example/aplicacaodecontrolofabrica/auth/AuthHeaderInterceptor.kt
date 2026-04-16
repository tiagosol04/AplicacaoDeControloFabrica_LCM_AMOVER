package com.example.aplicacaodecontrolofabrica.core.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adiciona automaticamente o token Bearer aos pedidos,
 * desde que:
 * - exista token válido em sessão
 * - o request ainda não tenha Authorization manual
 */
class AuthHeaderInterceptor(
    private val authDataStore: AuthDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Respeita pedidos que já tragam header manual.
        if (!original.header("Authorization").isNullOrBlank()) {
            return chain.proceed(original)
        }

        val token = try {
            runBlocking {
                authDataStore.token.first()
            }?.trim()
        } catch (_: Exception) {
            null
        }

        if (token.isNullOrBlank()) {
            return chain.proceed(original)
        }

        val authenticatedRequest = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}