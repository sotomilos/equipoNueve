package com.example.inventory.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.inventory.R

class AddProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar y flecha
        val toolbar: Toolbar = view.findViewById(R.id.toolbar_add_product)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        toolbar.setNavigationOnClickListener {
            // Regresa a la pantalla anterior (HomeInventoryFragment) haciendo pop del backstack
            parentFragmentManager.popBackStack()
        }

        // Boton "Guardar" (placeholder)
        val btnSave: Button = view.findViewById(R.id.btn_save_product)
        btnSave.setOnClickListener {
            // TODO: implementar validación y guardado usando ViewModel/Repository
            // Por ahora solo cerramos el fragment y volvemos:
            parentFragmentManager.popBackStack()
        }
    }
}

