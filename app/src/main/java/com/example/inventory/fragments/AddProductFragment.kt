package com.example.inventory.fragments

import android.os.Bundle
import android.text.InputFilter
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

        // --- Filtros reutilizables ---
        // Filtro: sólo dígitos (protege contra pegado de texto con letras)
        val onlyDigitsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (c.isDigit()) sb.append(c)
            }
            if (sb.length == end - start) null else sb.toString()
        }

        // --- Código producto (solo dígitos, max 4) ---
        val tietCode = view.findViewById<TextInputEditText>(R.id.tiet_product_code)
        val maxLenCode = InputFilter.LengthFilter(4)
        tietCode.filters = arrayOf(maxLenCode, onlyDigitsFilter)

        // --- Nombre artículo (max 40, permite letras, números, espacios y algunos signos) ---
        val tietName = view.findViewById<TextInputEditText>(R.id.tiet_product_name)
        val maxLenName = InputFilter.LengthFilter(40)
        val allowedCharsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (c.isLetterOrDigit() || c.isWhitespace() || c in listOf('-', '_', '.', ',')) {
                    sb.append(c)
                }
            }
            if (sb.length == end - start) null else sb.toString()
        }
        tietName.filters = arrayOf(maxLenName, allowedCharsFilter)

        // --- Precio (solo dígitos, max 20) ---
        val tietPrice = view.findViewById<TextInputEditText>(R.id.tiet_product_price)
        val maxLenPrice = InputFilter.LengthFilter(20)
        tietPrice.filters = arrayOf(maxLenPrice, onlyDigitsFilter)

        // --- Cantidad (solo dígitos, max 4) (CRITERIO 5) ---
        val tietQuantity = view.findViewById<TextInputEditText>(R.id.tiet_product_quantity)
        val maxLenQuantity = InputFilter.LengthFilter(4)
        tietQuantity.filters = arrayOf(maxLenQuantity, onlyDigitsFilter)

        // --- Guardar ---
        val btnSave = view.findViewById<Button>(R.id.btn_save_product)
        btnSave.setOnClickListener {
            val codeText = tietCode.text?.toString()?.trim() ?: ""
            val nameText = tietName.text?.toString()?.trim() ?: ""
            val priceText = tietPrice.text?.toString()?.trim() ?: ""
            val quantityText = tietQuantity.text?.toString()?.trim() ?: ""

            // Validaciones simples
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
            if (priceText.isEmpty()) {
                showToast("Ingresa el precio")
                return@setOnClickListener
            }
            if (priceText.length > 20) {
                showToast("El precio debe tener máximo 20 dígitos")
                return@setOnClickListener
            }
            if (quantityText.isEmpty()) {
                showToast("Ingresa la cantidad")
                return@setOnClickListener
            }
            if (quantityText.length > 4) {
                showToast("La cantidad debe tener máximo 4 dígitos")
                return@setOnClickListener
            }

            // TODO: integrar con tu ViewModel/Repo para guardar producto
            showToast("Guardado: $codeText — $nameText — $priceText — qty:$quantityText")

            // Cerrar fragment (si quieres)
            parentFragmentManager.popBackStack()
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}




