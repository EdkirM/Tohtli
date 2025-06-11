package com.example.tohtli2.ui.dashboard

// Importaciones para trabajar con ViewModel y LiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    // LiveData mutable privada que contiene el texto a mostrar
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment" // Valor inicial
    }

    // LiveData pública (inmutable) que expone el texto a la vista
    val text: LiveData<String> = _text
}
