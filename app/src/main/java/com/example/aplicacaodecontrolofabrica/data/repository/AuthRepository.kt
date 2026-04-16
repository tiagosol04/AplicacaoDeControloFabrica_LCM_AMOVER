package com.example.aplicacaodecontrolofabrica.data.repository

import com.example.aplicacaodecontrolofabrica.core.auth.UserSession

interface AuthRepository {
    suspend fun login(usernameOrEmail: String, password: String)
    suspend fun me(): UserSession
    suspend fun logout()
}