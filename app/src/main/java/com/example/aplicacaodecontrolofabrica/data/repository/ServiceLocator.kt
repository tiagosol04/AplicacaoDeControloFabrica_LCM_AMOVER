package com.example.aplicacaodecontrolofabrica.data.repository

import android.content.Context
import com.example.aplicacaodecontrolofabrica.core.auth.AuthDataStore
import com.example.aplicacaodecontrolofabrica.core.network.ApiModule
import com.example.aplicacaodecontrolofabrica.core.network.ApiService

object ServiceLocator {

    @Volatile
    private var initialized = false

    private val lock = Any()

    private lateinit var _authDataStore: AuthDataStore
    private lateinit var _apiService: ApiService
    private lateinit var _authRepository: AuthRepository
    private lateinit var _fabricaRepository: FabricaRepository

    val authDataStore: AuthDataStore
        get() {
            ensureInitialized("authDataStore")
            return _authDataStore
        }

    val apiService: ApiService
        get() {
            ensureInitialized("apiService")
            return _apiService
        }

    val authRepository: AuthRepository
        get() {
            ensureInitialized("authRepository")
            return _authRepository
        }

    val fabricaRepository: FabricaRepository
        get() {
            ensureInitialized("fabricaRepository")
            return _fabricaRepository
        }

    fun init(context: Context) {
        if (initialized) return

        synchronized(lock) {
            if (initialized) return

            val appContext = context.applicationContext

            _authDataStore = AuthDataStore(appContext)
            _apiService = ApiModule.createApiService(_authDataStore)

            _authRepository = AuthRepositoryImpl(
                api = _apiService,
                authDataStore = _authDataStore
            )

            _fabricaRepository = FabricaRepositoryImpl(
                api = _apiService
            )

            initialized = true
        }
    }

    private fun ensureInitialized(name: String) {
        if (!initialized ||
            !::_authDataStore.isInitialized ||
            !::_apiService.isInitialized ||
            !::_authRepository.isInitialized ||
            !::_fabricaRepository.isInitialized
        ) {
            throw IllegalStateException(
                "ServiceLocator não inicializado. Chama ServiceLocator.init(context) antes de usar $name."
            )
        }
    }
}