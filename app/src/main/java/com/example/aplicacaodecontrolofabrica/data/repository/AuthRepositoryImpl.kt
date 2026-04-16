package com.example.aplicacaodecontrolofabrica.data.repository

import com.example.aplicacaodecontrolofabrica.core.auth.AuthDataStore
import com.example.aplicacaodecontrolofabrica.core.auth.UserSession
import com.example.aplicacaodecontrolofabrica.core.network.ApiService
import com.example.aplicacaodecontrolofabrica.data.dto.LoginRequest
import com.example.aplicacaodecontrolofabrica.data.mapper.toUserSession

class AuthRepositoryImpl(
    private val api: ApiService,
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override suspend fun login(usernameOrEmail: String, password: String) {
        val response = api.login(
            LoginRequest(
                usernameOrEmail = usernameOrEmail.trim(),
                password = password
            )
        )

        authDataStore.save(
            token = response.token,
            userId = response.userId,
            username = response.username,
            email = response.email,
            roles = response.roles
        )

        runCatching {
            val me = api.me()

            val mergedRoles = when {
                me.roles.isNotEmpty() -> me.roles
                response.roles.isNotEmpty() -> response.roles
                else -> emptyList()
            }

            authDataStore.save(
                token = response.token,
                userId = me.userId,
                username = me.username,
                email = me.email ?: response.email,
                roles = mergedRoles
            )
        }
    }

    override suspend fun me(): UserSession {
        val me = api.me()
        return me.toUserSession()
    }

    override suspend fun logout() {
        authDataStore.clear()
    }
}