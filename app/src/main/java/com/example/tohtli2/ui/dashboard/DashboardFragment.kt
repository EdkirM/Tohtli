package com.example.tohtli2.ui.dashboard

// Importaciones necesarias para trabajar con fragmentos y vistas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tohtli2.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    // Variable privada para el *binding* (enlace con el XML)
    private var _binding: FragmentDashboardBinding? = null

    // Acceso seguro al binding no nulo
    private val binding get() = _binding!!

    // Se llama cuando se crea la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se obtiene una instancia del ViewModel asociado a este fragmento
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        // Se infla el layout y se guarda la referencia en _binding
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Se accede al TextView definido en el layout
        val textView: TextView = binding.textDashboard

        // Se observa el texto del ViewModel y se actualiza el TextView cuando cambia
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    // Se llama cuando la vista se destruye (para evitar fugas de memoria)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
