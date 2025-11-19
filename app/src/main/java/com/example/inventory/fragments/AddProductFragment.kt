package com.example.inventory.fragments

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.inventory.R
import com.google.android.material.textfield.TextInputEditText

class AddProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back arrow
        val ivBack = view.findViewById<ImageView>(R.id.iv_back)
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Campo código producto
        val tietCode = view.findViewById<TextInputEditText>(R.id.tiet_product_code)

        // Filtro: sólo dígitos (protege contra pegado de texto con letras)
        val onlyDigitsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (c.isDigit()) sb.append(c)
            }
            if (sb.length == end - start) null else sb.toString()
        }

        // Filtro longitud máxima para código (4)
        val maxLenCode = InputFilter.LengthFilter(4)
        tietCode.filters = arrayOf(maxLenCode, onlyDigitsFilter)

        // Campo nombre producto
        val tietName = view.findViewById<TextInputEditText>(R.id.tiet_product_name)
        // Filtro longitud máxima 40 (también definido en XML, pero reforzamos)
        val maxLenName = InputFilter.LengthFilter(40)
        // Si quieres evitar caracteres de control raros, puedes añadir un filtro que permita espacios y letras:
        val allowedCharsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                // permitimos letras, dígitos (por si acaso), espacios y signos básicos
                if (c.isLetterOrDigit() || c.isWhitespace() || c in listOf('-', '_', '.', ',')) {
                    sb.append(c)
                }
            }
            if (sb.length == end - start) null else sb.toString()
        }
        tietName.filters = arrayOf(maxLenName, allowedCharsFilter)

        // Botón guardar: validaciones básicas
        val btnSave = view.findViewById<Button>(R.id.btn_save_product)
        btnSave.setOnClickListener {
            val codeText = tietCode.text?.toString()?.trim() ?: ""
            val nameText = tietName.text?.toString()?.trim() ?: ""

            // Validaciones
            if (codeText.isEmpty()) {
                showToast("Ingresa el código del producto")
                return@setOnClickListener
            }
            if (codeText.length > 4) {
                showToast("El código debe tener máximo 4 dígitos")
                return@setOnClickListener
            }
            if (nameText.isEmpty()) {
                showToast("Ingresa el nombre del artículo")
                return@setOnClickListener
            }
            if (nameText.length > 40) {
                showToast("El nombre debe tener máximo 40 caracteres")
                return@setOnClickListener
            }

            // TODO: integrar con tu ViewModel / Repository para guardar el producto
            // Ejemplo placeholder:
            showToast("Guardado: $codeText — $nameText")

            // Cerrar fragment (si quieres)
            parentFragmentManager.popBackStack()
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}



