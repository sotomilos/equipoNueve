package com.example.inventory.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.R
import com.example.inventory.adapters.InventoryAdapter
import com.example.inventory.sessions.SessionManager
import com.example.inventory.ui.MainActivity
import com.example.inventory.viewmodel.InventoryViewModelC
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeInventoryFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private val viewModel: InventoryViewModelC by viewModels()
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
        sessionManager = SessionManager(requireContext())

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerview)
        progressBar = view.findViewById(R.id.pbCircular)

        setupRecyclerView()
        setupLogoutButton(view)
        setupFabAdd(view)

        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getListInventory()
    }

    private fun setupObservers() {
        viewModel.listInventory.observe(viewLifecycleOwner) { items ->
            Log.d("DEBUG_FRAGMENT", "LiveData recibió: ${items.size} items")
            Log.d("DEBUG_FRAGMENT", "Contenido: $items")
            inventoryAdapter.updateData(items)
        }

        viewModel.progressState.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        inventoryAdapter = InventoryAdapter(emptyList())
        recyclerView.adapter = inventoryAdapter
    }

    private fun setupLogoutButton(view: View) {
        val logoutButton: ImageView = view.findViewById(R.id.iv_logout)
        logoutButton.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(requireActivity(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun setupFabAdd(view: View) {
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fbagregar)
        fabAdd.setOnClickListener {

            val addFragment = AddProductFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, addFragment)
                .addToBackStack("add_product")
                .commit()
        }
    }
}
