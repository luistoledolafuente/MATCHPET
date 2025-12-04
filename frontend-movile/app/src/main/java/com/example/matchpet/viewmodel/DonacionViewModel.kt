package com.example.matchpet.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.donacion.CheckoutResponse
import com.example.matchpet.data.model.donacion.CreateDonacionRequest
import com.example.matchpet.data.model.donacion.DonacionResponse
import com.example.matchpet.utils.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

sealed class DonationState {
    object Idle : DonationState()
    object Loading : DonationState()
    data class CheckoutReady(val checkoutResponse: CheckoutResponse) : DonationState()
    data class DonationsLoaded(val donations: List<DonacionResponse>) : DonationState()
    data class Error(val message: String) : DonationState()
}

class DonacionViewModel : ViewModel() {

    private val _donationState = MutableStateFlow<DonationState>(DonationState.Idle)
    val donationState: StateFlow<DonationState> = _donationState

    fun createDonation(
        monto: BigDecimal,
        refugioId: Int? = null,
        animalId: Int? = null,
        mensaje: String? = null,
        token: String? = null
    ) {
        _donationState.value = DonationState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateDonacionRequest(
                    monto = monto,
                    moneda = "PEN",
                    refugioId = refugioId,
                    animalId = animalId,
                    mensajeDonante = mensaje
                )

                val response = if (token != null) {
                    Injection.apiService.createDonacion("Bearer $token", request)
                } else {
                    Injection.apiService.createDonacionAnonymous(request)
                }

                if (response.isSuccessful && response.body() != null) {
                    _donationState.value = DonationState.CheckoutReady(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    _donationState.value = DonationState.Error("Error ${response.code()}: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("DonacionViewModel", "Donation Exception", e)
                _donationState.value = DonationState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun loadMyDonations(token: String) {
        _donationState.value = DonationState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = Injection.apiService.getMisDonaciones("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    _donationState.value = DonationState.DonationsLoaded(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    _donationState.value = DonationState.Error("Error ${response.code()}: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("DonacionViewModel", "Load Donations Exception", e)
                _donationState.value = DonationState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun resetState() {
        _donationState.value = DonationState.Idle
    }
}
