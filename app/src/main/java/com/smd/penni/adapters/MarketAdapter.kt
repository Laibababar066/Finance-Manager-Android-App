package com.smd.penni.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smd.penni.R
import com.smd.penni.network.MarketCoin

class MarketAdapter(private var coins: List<MarketCoin>) : RecyclerView.Adapter<MarketAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(android.R.id.text1)
        val details: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coin = coins[position]
        holder.name.text = "${coin.name} (${coin.symbol.uppercase()})"
        holder.name.setTextColor(Color.WHITE)
        
        val change = coin.price_change_percentage_24h
        val changeText = String.format("%.2f", change) + "%"
        holder.details.text = "Price: $${coin.current_price} | 24h: $changeText"
        holder.details.setTextColor(if (change >= 0) Color.GREEN else Color.RED)
    }

    override fun getItemCount() = coins.size

    fun updateData(newList: List<MarketCoin>) {
        coins = newList
        notifyDataSetChanged()
    }
}
