package com.example.matchpet.viewmodel.refugio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.auth.UserProfileResponse
import com.example.matchpet.data.model.refugio.RefugioProfileResponse
import com.example.matchpet.data.model.refugio.RefugioUpdateRequest
import com.example.matchpet.data.repository.RefugioRepository
import com.example.matchpet.data.repository.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RefugioViewModel(
    private val repository: RefugioRepository
) : ViewModel() {

    // ------------------------------------------------------------
    // ESTADOS DEL PERFIL DEL REFUGIO
    // ------------------------------------------------------------
    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile

    private val _refugio = MutableStateFlow<RefugioProfileResponse?>(null)
    val refugio: StateFlow<RefugioProfileResponse?> = _refugio

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage


    // ------------------------------------------------------------
    // 1. OBTENER PERFIL COMPLETO (USER + REFUGIO)
    // ------------------------------------------------------------
    fun loadProfile(token: String) {
        viewModelScope.launch {
            repository.getProfile(token).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _loading.value = true

                    is Resource.Success -> {
                        _loading.value = false
                        _userProfile.value = resource.data

                        // ⭐ extraemos refugio desde el perfil
                        _refugio.value = resource.data?.refugio
                    }

                    is Resource.Error -> {
                        _loading.value = false
                        _errorMessage.value = resource.message ?: "Error desconocido"
                    }
                }
            }
        }
    }


    // ------------------------------------------------------------
    // 2. ACTUALIZAR PERFIL DEL REFUGIO
    // ------------------------------------------------------------
    fun updateRefugio(
        refugioId: Int,
        token: String,
        request: RefugioUpdateRequest
    ) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val result = repository.updateRefugioProfile(
                id = refugioId,
                token = token,
                request = request
            )

            _loading.value = false

            when (result) {
                is Resource.Success -> {
                    _refugio.value = result.data
                    _successMessage.value = "Datos actualizados correctamente."
                }

                is Resource.Error -> {
                    _errorMessage.value = result.message
                }

                else -> Unit
            }
        }
    }
}