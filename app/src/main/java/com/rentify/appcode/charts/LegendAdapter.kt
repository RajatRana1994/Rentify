package com.rentify.appcode.charts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rentify.R
import com.rentify.RentifyApp.Companion.colors
import com.rentify.local.ChartLegend
import com.rentify.util.Extensions.Companion.addFormatDecimal
import com.rentify.util.Extensions.Companion.clearFormat
import java.util.*

class LegendAdapter(val legendList: MutableList<ChartLegend>, val isAmountFormat: Boolean = true, val yearType: Boolean = true) :
    RecyclerView.Adapter<LegendAdapter.VHolder>() {

    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val bgColor = view.findViewById<TextView>(R.id.bgColor)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvAmount = view.findViewById<TextView>(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_legends, parent, false)
        )
    }

    override fun getItemCount(): Int = legendList.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            bgColor.setBackgroundColor(colors[position])
            bgColor.visibility=if (yearType) View.VISIBLE else View.GONE
            tvName.text = legendList[position].name ?: ""
            if (isAmountFormat)
                tvAmount.text =
                    (legendList[position].amount ?: "0.00").clearFormat().addFormatDecimal()
            else{
                var str=(legendList[position].amount ?: "0").clearFormat()
                if (str.contains(".") && (str.substringAfter(".").length <3)) {
                    val first = str.substringBefore(".")
                    val sec = str.substringAfter(".").replace(".","")
                    str = String.format(Locale.getDefault(), "%.2f", ("$first.$sec").toDouble())
                }else if (str.contains(".") && (str.substringAfter(".").length > 2)) {
                    val first = str.substringBefore(".")
                    val sec = str.substringAfter(".").replace(".","").substring(0,3)
                    str = String.format(Locale.getDefault(), "%.2f", ("$first.$sec").toDouble())
                }
                tvAmount.text = "$str%"
            }
        }
    }


}