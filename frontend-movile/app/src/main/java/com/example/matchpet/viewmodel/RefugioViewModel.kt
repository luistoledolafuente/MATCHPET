package com.example.matchpet.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

data class Animal(val nombre: String, val raza: String?)
data class BitacoraItem(val descripcion: String, val fecha: Date)
data class User(val nombreCompleto: String)

class RefugioDashboardViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _animales = MutableStateFlow<List<Animal>>(emptyList())
    val animales: StateFlow<List<Animal>> = _animales.asStateFlow()

    private val _bitacora = MutableStateFlow<List<BitacoraItem>>(emptyList())
    val bitacora: StateFlow<List<BitacoraItem>> = _bitacora.asStateFlow()

    fun loadUser(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _user.value = User("Refugio Esperanza")
        }
    }

    fun loadAnimales(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _animales.value = listOf(
                Animal("Max", "Perro"),
                Animal("Luna", "Gato"),
                Animal("Rocky", "Perro"),
                Animal("Mimi", "Gato"),
                Animal("Thor", "Perro")
            )
        }
    }

    fun loadBitacora(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _bitacora.value = listOf(
                BitacoraItem("Max fue adoptado", Date()),
                BitacoraItem("Nueva mascota agregada: Luna", Date()),
                BitacoraItem("Donación recibida", Date())
            )
        }
    }
}
