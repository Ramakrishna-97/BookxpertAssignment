package com.example.assessment_asset_telematics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assessment_asset_telematics.model.Account

class RecyclerAdapter(private val list: List<Account>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.tvAccountName.text = item.ActName
        holder.textView.text =item.alterName ?: "No Alternate Name"

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAccountName: TextView = itemView.findViewById(R.id.tvAccountName)
        val textView: TextView = itemView.findViewById(R.id.tvAlternateName)
    }
}
