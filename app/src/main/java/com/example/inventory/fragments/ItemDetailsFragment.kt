package com.example.inventory.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.inventory.R
import com.example.inventory.model.Inventory
import com.example.inventory.viewmodel.InventoryViewModelC
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class ItemDetailsFragment : Fragment() {

    private val viewModel: InventoryViewModelC by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainLayout = view.findViewById<View>(R.id.detail_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inventoryItem = arguments?.getSerializable("inventory_item") as? Inventory

        view.findViewById<MaterialToolbar>(R.id.toolbarDetails).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        inventoryItem?.let { item ->
            view.findViewById<TextView>(R.id.tvItem).text = item.name
            view.findViewById<TextView>(R.id.tvValorUnidad).text = "$${item.price}"
            view.findViewById<TextView>(R.id.tvCantidad).text = "${item.quantity}"
            view.findViewById<TextView>(R.id.tvSumaTotal).text = "$${item.quantity * item.price}"

            view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar Producto")
                    .setMessage("Estás seguro de eliminar el producto: ${item.name}?")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.deleteInventory(item)
                        parentFragmentManager.popBackStack()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            view.findViewById<FloatingActionButton>(R.id.fabEdit).setOnClickListener {
                val fragment = UpdateProductFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("inventory_item", item)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}
