package com.example.aplicacaodecontrolofabrica.core.network

import com.example.aplicacaodecontrolofabrica.core.auth.AuthDataStore
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUiMessage(
    authDataStore: AuthDataStore,
    fallback: String = "Ocorreu um erro inesperado."
): String {
    @Suppress("UNUSED_VARIABLE")
    val unused = authDataStore

    return when (this) {
        is SocketTimeoutException ->
            "O servidor demorou demasiado tempo a responder."

        is UnknownHostException ->
            "Não foi possível contactar o servidor. Verifica a ligação ou o endereço configurado."

        is IOException ->
            "Sem ligação à internet ou falha de comunicação com o servidor."

        is HttpException -> when (code()) {
            401 -> "Sessão expirada. Inicia sessão novamente."
            403 -> "Sem permissões para esta ação."
            404 -> "Recurso não encontrado."
            409 -> "Conflito de dados. Verifica se o registo já existe ou se foi alterado."
            500, 502, 503, 504 -> "Servidor indisponível. Tenta novamente mais tarde."
            else -> "Erro HTTP ${code()}."
        }

        else -> this.message?.takeIf { it.isNotBlank() } ?: fallback
    }
}