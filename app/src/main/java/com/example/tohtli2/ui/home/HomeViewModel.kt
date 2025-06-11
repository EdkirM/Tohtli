package com.example.tohtli2.ui.home

// Importaciones necesarias para usar LiveData y ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Clase que representa el ViewModel para el HomeFragment
class HomeViewModel : ViewModel() {

    // LiveData mutable privada que almacena el texto
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"  // Valor inicial del texto
    }

    // LiveData pública e inmutable que expone el texto al fragmento
    val text: LiveData<String> = _text
}
