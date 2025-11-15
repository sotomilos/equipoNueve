package com.example.inventory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.R
import com.example.inventory.model.Inventory

class InventoryAdapter(private var items: List<Inventory>) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Inventory>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        private val quantityTextView: TextView = itemView.findViewById(R.id.tv_item_quantity)
        private val priceTextView: TextView = itemView.findViewById(R.id.tv_item_price)

        fun bind(item: Inventory) {
            nameTextView.text = item.name
            quantityTextView.text = "Quantity: ${item.quantity}"
            priceTextView.text = "Price: $${item.price}"
        }
    }
}
