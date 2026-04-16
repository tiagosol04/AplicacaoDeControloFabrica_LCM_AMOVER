package com.example.aplicacaodecontrolofabrica.core.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Se o backend devolver 401 num pedido autenticado,
 * limpamos a sessão local para obrigar o regresso ao Login.
 *
 * Isto encaixa bem com a AppNavigation, que observa o token
 * e redireciona automaticamente quando a sessão deixa de existir.
 */
class AuthFailureInterceptor(
    private val authDataStore: AuthDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val hadAuthHeader = !request.header("Authorization").isNullOrBlank()

        if (response.code == 401 && hadAuthHeader) {
            runCatching {
                runBlocking {
                    authDataStore.clear()
                }
            }
        }

        return response
    }
}