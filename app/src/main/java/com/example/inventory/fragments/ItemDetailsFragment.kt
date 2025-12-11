package com.example.inventory.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.inventory.R
import com.example.inventory.model.Inventory
import com.example.inventory.viewmodel.InventoryViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class ItemDetailsFragment : Fragment() {

    private val viewModel: InventoryViewModel by viewModels()
    private var currentInventoryItem: Inventory? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbarDetails).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val itemId = arguments?.getString("inventory_item_id")
        if (itemId == null) {
            parentFragmentManager.popBackStack()
            return
        }

        // Observe the item from the ViewModel for real-time updates
        viewModel.getItem(itemId).observe(viewLifecycleOwner) { inventoryItem ->
            inventoryItem?.let {
                currentInventoryItem = it
                bindDataToViews(view, it)
            }
        }
    }

    private fun bindDataToViews(view: View, item: Inventory) {
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0.00", symbols)

        view.findViewById<TextView>(R.id.tvItem).text = item.name
        view.findViewById<TextView>(R.id.tvValorUnidad).text = "$ ${formatter.format(item.price)}"
        view.findViewById<TextView>(R.id.tvCantidad).text = item.quantity.toString()
        view.findViewById<TextView>(R.id.tvSumaTotal).text = "$ ${formatter.format(item.quantity * item.price)}"

        view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            showDeleteConfirmationDialog(item)
        }

        view.findViewById<FloatingActionButton>(R.id.fabEdit).setOnClickListener {
            currentInventoryItem?.let { currentItem ->
                val fragment = UpdateProductFragment().apply {
                    arguments = Bundle().apply { putParcelable("inventory_item", currentItem) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun showDeleteConfirmationDialog(item: Inventory) {
        AlertDialog.Builder(requireContext())
            .setTitle("Borrar producto")
            .setMessage("Estas seguro de borrar '${item.name}'?")
            .setPositiveButton("Borrar") { _, _ ->
                viewModel.deleteInventoryItem(item)
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
