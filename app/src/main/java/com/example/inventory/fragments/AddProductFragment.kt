package com.example.inventory.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.inventory.R
import com.example.inventory.model.Inventory
import com.example.inventory.viewmodel.InventoryViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private lateinit var tietCode: TextInputEditText
    private lateinit var tietName: TextInputEditText
    private lateinit var tietPrice: TextInputEditText
    private lateinit var tietQuantity: TextInputEditText
    private lateinit var btnSave: Button

    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivBack = view.findViewById<ImageView>(R.id.iv_back)
        tietCode = view.findViewById(R.id.tiet_product_code)
        tietName = view.findViewById(R.id.tiet_product_name)
        tietPrice = view.findViewById(R.id.tiet_product_price)
        tietQuantity = view.findViewById(R.id.tiet_product_quantity)
        btnSave = view.findViewById(R.id.btn_save_product)

        ivBack.setOnClickListener { parentFragmentManager.popBackStack() }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        tietCode.addTextChangedListener(textWatcher)
        tietName.addTextChangedListener(textWatcher)
        tietPrice.addTextChangedListener(textWatcher)
        tietQuantity.addTextChangedListener(textWatcher)

        btnSave.setOnClickListener {
            val code = tietCode.text.toString().trim()
            val name = tietName.text.toString().trim()
            val price = tietPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
            val quantity = tietQuantity.text.toString().trim().toIntOrNull() ?: 0

            if (name.isNotBlank()) {
                inventoryViewModel.saveInventoryItem(Inventory(id = code, name = name, price = price, quantity = quantity))
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Product name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        updateSaveButtonState()
    }

    private fun updateSaveButtonState() {
        val code = tietCode.text.toString().trim()
        val name = tietName.text.toString().trim()
        val price = tietPrice.text.toString().trim()
        val quantity = tietQuantity.text.toString().trim()

        val allFieldsFilled = code.isNotEmpty() && name.isNotEmpty() && price.isNotEmpty() && quantity.isNotEmpty()

        btnSave.isEnabled = allFieldsFilled
        btnSave.alpha = if (allFieldsFilled) 1.0f else 0.4f
    }
}
