package com.example.inventory.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.inventory.R
import com.google.android.material.textfield.TextInputEditText

class AddProductFragment : Fragment() {

    private lateinit var tietCode: TextInputEditText
    private lateinit var tietName: TextInputEditText
    private lateinit var tietPrice: TextInputEditText
    private lateinit var tietQuantity: TextInputEditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // find views
        val ivBack = view.findViewById<ImageView>(R.id.iv_back)
        tietCode = view.findViewById(R.id.tiet_product_code)
        tietName = view.findViewById(R.id.tiet_product_name)
        tietPrice = view.findViewById(R.id.tiet_product_price)
        tietQuantity = view.findViewById(R.id.tiet_product_quantity)
        btnSave = view.findViewById(R.id.btn_save_product)

        // back action
        ivBack.setOnClickListener { parentFragmentManager.popBackStack() }

        // --- filtros reutilizables ---
        val onlyDigitsFilter = InputFilter { source, start, end, _, _, _ ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (c.isDigit()) sb.append(c)
            }
            if (sb.length == end - start) null else sb.toString()
        }

        val allowedCharsFilter = InputFilter { source, start, end, _, _, _ ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (c.isLetterOrDigit() || c.isWhitespace() || c in listOf('-', '_', '.', ',')) {
                    sb.append(c)
                }
            }
            if (sb.length == end - start) null else sb.toString()
        }

        // apply input filters (length + content)
        tietCode.filters = arrayOf(InputFilter.LengthFilter(4), onlyDigitsFilter)
        tietName.filters = arrayOf(InputFilter.LengthFilter(40), allowedCharsFilter)
        tietPrice.filters = arrayOf(InputFilter.LengthFilter(20), onlyDigitsFilter)
        tietQuantity.filters = arrayOf(InputFilter.LengthFilter(4), onlyDigitsFilter)

        // TextWatcher para actualizar estado del botón
        val tw = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
        }

        tietCode.addTextChangedListener(tw)
        tietName.addTextChangedListener(tw)
        tietPrice.addTextChangedListener(tw)
        tietQuantity.addTextChangedListener(tw)

        // estado inicial
        updateSaveButtonState()

        // acción Guardar
        btnSave.setOnClickListener {
            val code = tietCode.text?.toString()?.trim()
            val name = tietName.text?.toString()?.trim()
            val price = tietPrice.text?.toString()?.trim()
            val qty = tietQuantity.text?.toString()?.trim()

            if (code.isNullOrEmpty() || name.isNullOrEmpty() || price.isNullOrEmpty() || qty.isNullOrEmpty()) {
                showToast("Completa todos los campos antes de guardar")
                return@setOnClickListener
            }

            // Validaciones finales
            if (code.length > 4) { showToast("Código: máximo 4 dígitos"); return@setOnClickListener }
            if (name.length > 40) { showToast("Nombre: máximo 40 caracteres"); return@setOnClickListener }
            if (price.length > 20) { showToast("Precio: máximo 20 dígitos"); return@setOnClickListener }
            if (qty.length > 4) { showToast("Cantidad: máximo 4 dígitos"); return@setOnClickListener }

            // TODO: integrar con ViewModel/Repo para persistir el producto
            showToast("Guardado: $code - $name - $price - qty:$qty")

            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Actualiza el estado del botón Guardar:
     * - Si todos los campos tienen texto: habilita el botón, cambia fondo a naranja,
     *   establece texto en blanco y en bold.
     * - Si falta algún campo: deshabilita el botón, fondo gris y texto en normal.
     */
    private fun updateSaveButtonState() {
        val code = tietCode.text?.toString()?.trim()
        val name = tietName.text?.toString()?.trim()
        val price = tietPrice.text?.toString()?.trim()
        val qty = tietQuantity.text?.toString()?.trim()

        val allFilled = !code.isNullOrEmpty()
                && !name.isNullOrEmpty()
                && !price.isNullOrEmpty()
                && !qty.isNullOrEmpty()

        if (allFilled) {
            // habilitado: fondo naranja sólido + texto BLANCO BOLD
            btnSave.isEnabled = true
            btnSave.isClickable = true
            btnSave.alpha = 1f
            btnSave.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_orange)
            // asegurar color blanco y bold
            btnSave.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            btnSave.setTypeface(null, Typeface.BOLD)
        } else {
            // deshabilitado: fondo gris + texto normal
            btnSave.isEnabled = false
            btnSave.isClickable = false
            btnSave.alpha = 0.4f
            btnSave.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_disabled)
            // mantener texto visible pero sin negrita
            btnSave.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            btnSave.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}





