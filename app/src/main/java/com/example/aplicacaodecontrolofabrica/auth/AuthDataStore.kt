package com.example.aplicacaodecontrolofabrica.core.auth

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "auth_session")

class AuthDataStore(private val context: Context) {

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_ROLES = stringSetPreferencesKey("roles")
    }

    private val prefsFlow = context.dataStore.data.catch { ex ->
        if (ex is IOException) emit(emptyPreferences()) else throw ex
    }

    /**
     * Null quando não existe token ou quando está vazio.
     */
    val token: Flow<String?> =
        prefsFlow
            .map { it[KEY_TOKEN] }
            .map { it?.trim()?.takeIf { v -> v.isNotBlank() } }

    val username: Flow<String?> =
        prefsFlow
            .map { it[KEY_USERNAME] }
            .map { it?.trim()?.takeIf { v -> v.isNotBlank() } }

    val email: Flow<String?> =
        prefsFlow
            .map { it[KEY_EMAIL] }
            .map { it?.trim()?.takeIf { v -> v.isNotBlank() } }

    val userId: Flow<String?> =
        prefsFlow
            .map { it[KEY_USER_ID] }
            .map { it?.trim()?.takeIf { v -> v.isNotBlank() } }

    val roles: Flow<List<String>> =
        prefsFlow.map { prefs ->
            prefs[KEY_ROLES]
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.sorted()
                ?: emptyList()
        }

    /**
     * Útil para a navegação saber se deve abrir login ou a app.
     */
    val isLoggedIn: Flow<Boolean> =
        token.map { !it.isNullOrBlank() }

    /**
     * Sessão agregada.
     * Se não houver token, devolve null.
     */
    val sessionFlow: Flow<UserSession?> =
        combine(token, userId, username, email, roles) { tok, id, user, mail, rs ->
            if (tok.isNullOrBlank()) {
                null
            } else {
                UserSession(
                    userId = id ?: "",
                    username = user ?: "Utilizador",
                    email = mail ?: "",
                    roles = rs
                )
            }
        }

    suspend fun save(
        token: String?,
        userId: String?,
        username: String?,
        email: String?,
        roles: List<String>?
    ) {
        context.dataStore.edit { prefs ->
            prefs.setOrRemove(KEY_TOKEN, token)
            prefs.setOrRemove(KEY_USER_ID, userId)
            prefs.setOrRemove(KEY_USERNAME, username)
            prefs.setOrRemove(KEY_EMAIL, email)

            val cleanRoles = roles
                .orEmpty()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toSet()

            if (cleanRoles.isEmpty()) {
                prefs.remove(KEY_ROLES)
            } else {
                prefs[KEY_ROLES] = cleanRoles
            }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    private fun MutablePreferences.setOrRemove(
        key: Preferences.Key<String>,
        value: String?
    ) {
        val clean = value?.trim()
        if (clean.isNullOrBlank()) {
            remove(key)
        } else {
            this[key] = clean
        }
    }
}