package com.example.aplicacaodecontrolofabrica.features.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.model.RoleAccessUi
import com.example.aplicacaodecontrolofabrica.data.model.perfilLabel
import com.example.aplicacaodecontrolofabrica.data.model.resolveRoleAccess
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import com.example.aplicacaodecontrolofabrica.data.repository.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val isLoading: Boolean = true,
    val nome: String = "Utilizador",
    val email: String = "",
    val roles: List<String> = emptyList(),
    val perfilLabel: String = "Perfil genérico",
    val access: RoleAccessUi? = null
)

class PerfilRealViewModel(
    private val fabricaRepository: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        observarSessao()
    }

    private fun observarSessao() {
        viewModelScope.launch {
            ServiceLocator.authDataStore.sessionFlow.collect { session ->
                if (session == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            nome = "Utilizador",
                            email = "",
                            roles = emptyList(),
                            perfilLabel = "Sem sessão",
                            access = null
                        )
                    }
                } else {
                    val access = resolveRoleAccess(session.roles)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            nome = session.username.ifBlank { "Utilizador" },
                            email = session.email,
                            roles = session.roles,
                            perfilLabel = access.perfilLabel(),
                            access = access
                        )
                    }
                }
            }
        }
    }
}