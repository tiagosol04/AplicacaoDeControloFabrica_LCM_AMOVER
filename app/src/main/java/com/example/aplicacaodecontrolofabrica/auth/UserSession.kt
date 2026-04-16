package com.example.aplicacaodecontrolofabrica.core.auth

data class UserSession(
    val userId: String,
    val username: String,
    val email: String,
    val roles: List<String> = emptyList()
) {
    fun hasRole(role: String): Boolean =
        roles.any { it.trim().equals(role.trim(), ignoreCase = true) }
}