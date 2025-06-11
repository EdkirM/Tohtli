package com.example.tohtli2.ui.home

// Importaciones necesarias para manejar fragmentos y vistas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tohtli2.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    // Variable de binding (permite acceder directamente a las vistas del layout)
    private var _binding: FragmentHomeBinding? = null

    // Asegura acceso no nulo al binding entre onCreateView y onDestroyView
    private val binding get() = _binding!!

    // Método que se ejecuta para inflar la interfaz de usuario del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se crea y obtiene una instancia del ViewModel asociado a este fragmento
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        // Se infla el layout XML y se guarda en el binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Se obtiene el TextView del layout y se vincula al texto del ViewModel
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it // Actualiza el texto cuando el LiveData cambia
        }

        return root // Se devuelve la vista raíz
    }

    // Método llamado cuando la vista del fragmento se destruye
    // Se libera el binding para evitar fugas de memoria
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
