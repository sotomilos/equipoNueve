package com.example.inventory.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.inventory.R
import com.example.inventory.model.Inventory
import com.example.inventory.viewmodel.InventoryViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateProductFragment : Fragment() {

    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var inventoryItem: Inventory

    private lateinit var etProductName: TextInputEditText
    private lateinit var etProductPrice: TextInputEditText
    private lateinit var etProductQuantity: TextInputEditText
    private lateinit var btnUpdate: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The Inventory item is now passed as a Parcelable
        inventoryItem = arguments?.getParcelable("inventory_item")!!

        val toolbar: Toolbar = view.findViewById(R.id.toolbar_update)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        val tvProductId: TextView = view.findViewById(R.id.tv_product_id)
        tvProductId.text = inventoryItem.id

        etProductName = view.findViewById(R.id.et_product_name)
        etProductPrice = view.findViewById(R.id.et_product_price)
        etProductQuantity = view.findViewById(R.id.et_product_quantity)
        btnUpdate = view.findViewById(R.id.btn_update)

        etProductName.setText(inventoryItem.name)
        etProductPrice.setText(inventoryItem.price.toString())
        etProductQuantity.setText(inventoryItem.quantity.toString())

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateFields()
            }
        }

        etProductName.addTextChangedListener(textWatcher)
        etProductPrice.addTextChangedListener(textWatcher)
        etProductQuantity.addTextChangedListener(textWatcher)

        btnUpdate.setOnClickListener { updateProduct() }

        validateFields()
    }

    private fun validateFields() {
        val name = etProductName.text.toString().trim()
        val price = etProductPrice.text.toString().trim()
        val quantity = etProductQuantity.text.toString().trim()

        btnUpdate.isEnabled = name.isNotEmpty() && price.isNotEmpty() && quantity.isNotEmpty()
    }

    private fun updateProduct() {
        val name = etProductName.text.toString().trim()
        val price = etProductPrice.text.toString().toDoubleOrNull() ?: inventoryItem.price
        val quantity = etProductQuantity.text.toString().toIntOrNull() ?: inventoryItem.quantity

        val updatedInventory = inventoryItem.copy(name = name, price = price, quantity = quantity)
        viewModel.updateInventoryItem(updatedInventory)

        parentFragmentManager.popBackStack()
    }
}
