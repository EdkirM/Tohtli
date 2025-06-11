package com.example.tohtli2.ui.notifications

// Importaciones necesarias para usar LiveData y ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel que proporciona datos al NotificationsFragment
class NotificationsViewModel : ViewModel() {

    // LiveData mutable privada que contiene el texto
    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment" // Texto inicial que se mostrará
    }

    // LiveData pública (solo lectura) que expone el texto al fragmento
    val text: LiveData<String> = _text
}
