package com.rentify.appcode.summary

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.rentify.R
import com.rentify.RentifyApp.Companion.colors
import com.rentify.local.ChartLegend
import com.rentify.util.Extensions.Companion.addFormatDecimal
import com.rentify.util.Extensions.Companion.clearFormat
import java.io.File

class PropertyImgAdapter(val imgList: MutableList<String>, val isAmountFormat: Boolean = true, val yearType: Boolean = true) :
    RecyclerView.Adapter<PropertyImgAdapter.VHolder>() {

    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val ivPropertyImg = view.findViewById<RoundedImageView>(R.id.ivPropertyImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_img, parent, false)
        )
    }

    override fun getItemCount(): Int = imgList.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            ivPropertyImg.setImageURI(Uri.fromFile(File(imgList[position])))
        }
    }


}