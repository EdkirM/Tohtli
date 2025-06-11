package com.example.tohtli2.ui.notifications

// Importaciones necesarias para trabajar con fragmentos y vistas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tohtli2.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    // Variable privada para el View Binding (se usa para acceder al layout)
    private var _binding: FragmentNotificationsBinding? = null

    // Propiedad pública que garantiza acceso no nulo al binding entre onCreateView y onDestroyView
    private val binding get() = _binding!!

    // Método que se ejecuta para inflar el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se obtiene una instancia del ViewModel correspondiente
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        // Se infla el layout utilizando ViewBinding
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Se vincula el TextView al LiveData del ViewModel
        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it  // Actualiza el texto cuando cambia el valor observado
        }

        return root  // Se retorna la vista raíz
    }

    // Libera los recursos del binding cuando se destruye la vista del fragmento
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
