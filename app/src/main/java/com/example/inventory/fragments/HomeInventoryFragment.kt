package com.example.inventory.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.R
import com.example.inventory.adapters.InventoryAdapter
import com.example.inventory.ui.MainActivity
import com.example.inventory.viewmodel.InventoryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeInventoryFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        recyclerView = view.findViewById(R.id.recyclerview)
        progressBar = view.findViewById(R.id.pbCircular)

        setupRecyclerView()
        setupLogoutButton(view)
        setupFabAdd(view)
        setupObservers()
        setupSwipeToDelete()
    }

    private fun setupObservers() {
        inventoryViewModel.inventoryItems.observe(viewLifecycleOwner) { items ->
            inventoryAdapter.updateData(items)
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        inventoryAdapter = InventoryAdapter(emptyList()) { inventory ->
            val fragment = ItemDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("inventory_item_id", inventory.id)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = inventoryAdapter
    }

    private fun setupSwipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = inventoryAdapter.getItemAt(viewHolder.adapterPosition)
                inventoryViewModel.deleteInventoryItem(item)

                Snackbar.make(requireView(), "Item Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") { inventoryViewModel.saveInventoryItem(item) }
                    .show()
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun setupLogoutButton(view: View) {
        val logoutButton: ImageView = view.findViewById(R.id.iv_logout)
        logoutButton.setOnClickListener {
            auth.signOut()
            (activity as? MainActivity)?.showLogin()
        }
    }

    private fun setupFabAdd(view: View) {
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fbagregar)
        fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, AddProductFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
