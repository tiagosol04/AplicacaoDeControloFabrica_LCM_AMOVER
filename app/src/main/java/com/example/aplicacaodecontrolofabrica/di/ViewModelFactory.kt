package com.example.aplicacaodecontrolofabrica.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacaodecontrolofabrica.data.repository.ServiceLocator
import com.example.aplicacaodecontrolofabrica.features.alertas.AlertasViewModel
import com.example.aplicacaodecontrolofabrica.features.cockpit.DashboardRealViewModel
import com.example.aplicacaodecontrolofabrica.features.encomendas.EncomendaDetalheViewModel
import com.example.aplicacaodecontrolofabrica.features.encomendas.EncomendasViewModel
import com.example.aplicacaodecontrolofabrica.features.equipa.EquipaViewModel
import com.example.aplicacaodecontrolofabrica.features.historico.HistoricoViewModel
import com.example.aplicacaodecontrolofabrica.features.login.AuthViewModel
import com.example.aplicacaodecontrolofabrica.features.operacao.OrdemDetalheRealViewModel
import com.example.aplicacaodecontrolofabrica.features.operacao.OrdensRealViewModel
import com.example.aplicacaodecontrolofabrica.features.perfil.PerfilRealViewModel
import com.example.aplicacaodecontrolofabrica.features.servicos.ServicoDetalheViewModel
import com.example.aplicacaodecontrolofabrica.features.servicos.ServicosViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {

    private val authRepository get() = ServiceLocator.authRepository
    private val authDataStore get() = ServiceLocator.authDataStore
    private val fabricaRepository get() = ServiceLocator.fabricaRepository

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(authRepository = authRepository, authDataStore = authDataStore) as T

            modelClass.isAssignableFrom(DashboardRealViewModel::class.java) ->
                DashboardRealViewModel(fabricaRepository = fabricaRepository) as T

            modelClass.isAssignableFrom(OrdensRealViewModel::class.java) ->
                OrdensRealViewModel(fabricaRepository = fabricaRepository) as T

            modelClass.isAssignableFrom(OrdemDetalheRealViewModel::class.java) ->
                OrdemDetalheRealViewModel(fabricaRepository = fabricaRepository) as T

            modelClass.isAssignableFrom(AlertasViewModel::class.java) ->
                AlertasViewModel(fabricaRepository = fabricaRepository) as T

            modelClass.isAssignableFrom(HistoricoViewModel::class.java) ->
                HistoricoViewModel(fabricaRepository = fabricaRepository) as T

            modelClass.isAssignableFrom(EquipaViewModel::class.java) ->
                EquipaViewModel(fabricaRepository = fabricaRepository) as T

            modelClass.isAssignableFrom(PerfilRealViewModel::class.java) ->
                PerfilRealViewModel(fabricaRepository = fabricaRepository) as T

            // ── Novos ViewModels ──
            modelClass.isAssignableFrom(ServicosViewModel::class.java) ->
                ServicosViewModel(repo = fabricaRepository) as T

            modelClass.isAssignableFrom(ServicoDetalheViewModel::class.java) ->
                ServicoDetalheViewModel(repo = fabricaRepository) as T

            modelClass.isAssignableFrom(EncomendasViewModel::class.java) ->
                EncomendasViewModel(repo = fabricaRepository) as T

            modelClass.isAssignableFrom(EncomendaDetalheViewModel::class.java) ->
                EncomendaDetalheViewModel(repo = fabricaRepository) as T

            else -> throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
        }
    }
}
