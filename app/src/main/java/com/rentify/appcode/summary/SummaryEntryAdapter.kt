package com.rentify.appcode.summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rentify.R
import com.rentify.util.Extensions.Companion.clearFormat

class SummaryEntryAdapter(
    val entryData: MutableList<Pair<String, String>>,
    val isAmountFormat: Boolean = true,
    val yearType: Boolean = true
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TITLE = 0
    val NORMAL = 1

    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount = view.findViewById<TextView>(R.id.tvAmount)
        val tvName = view.findViewById<TextView>(R.id.tvName)
    }

    class TitleHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            if (viewType == TITLE) R.layout.item_summary_title else R.layout.item_summary_normal,
            parent,
            false
        )
        return if (viewType == TITLE) TitleHolder(view) else VHolder(view)
    }

    override fun getItemCount(): Int = entryData.size

    override fun getItemViewType(position: Int): Int {
        return if (entryData[position].second.isEmpty()) TITLE else NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            when (this) {
                is TitleHolder -> {
                    tvName.text = entryData[position].first
                }
                is VHolder -> {
                    tvName.text = entryData[position].first
                    tvAmount.text = entryData[position].second

                    if (position>entryData.size-4)
                        tvAmount.setTextColor(
                            ContextCompat.getColor(
                                tvAmount.context,
                                if (entryData[position].second.clearFormat()
                                        .toDouble() < 0
                                ) R.color.red else R.color.forest_green
                            )
                        )
                }
                }
            }
        }


}