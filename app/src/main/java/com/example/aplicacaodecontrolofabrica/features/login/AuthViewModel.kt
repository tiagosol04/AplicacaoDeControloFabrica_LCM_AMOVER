package com.example.aplicacaodecontrolofabrica.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.core.auth.AuthDataStore
import com.example.aplicacaodecontrolofabrica.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToApp: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val token: Flow<String> = authDataStore.token.map { it.orEmpty() }
    val roles: Flow<List<String>> = authDataStore.roles

    fun login(usernameOrEmail: String, password: String) {
        val usernameClean = usernameOrEmail.trim()
        val passwordClean = password.trim()

        if (usernameClean.isBlank() || passwordClean.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Preenche o utilizador/email e a palavra-passe."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    navigateToApp = false
                )
            }

            runCatching {
                authRepository.login(usernameClean, passwordClean)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        navigateToApp = true
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Não foi possível iniciar sessão.",
                        navigateToApp = false
                    )
                }
            }
        }
    }

    fun consumeNavigation() {
        _uiState.update { it.copy(navigateToApp = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = null,
                    navigateToApp = false
                )
            }
        }
    }
}