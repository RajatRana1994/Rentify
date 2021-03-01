package com.rentify.util

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import com.rentify.R

public class ProgressDialogs {

    companion object {
        private var mProgressDialog: ProgressDialog? = null

    fun showLoading(context:Context,title:String="") {
        hideLoading()
        mProgressDialog = initProgressDialog(context,title)
    }

    fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }

    }
    fun initProgressDialog(context: Context, title: String): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.spinner_loader_dialog)
        if (title.isEmpty().not()) progressDialog.findViewById<TextView>(R.id.loaderTitle).text= title
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }
/*
    fun init(context: Activity): ProgressDialog? {
        loader = ProgressDialog(context)
        if (loader!!.window != null) {
            loader!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        loader!!.setContentView(R.layout.progress_loader_view)
        loader!!.window!!.setGravity(Gravity.CENTER)
        loader!!.isIndeterminate = true
        loader!!.setCancelable(false)
        loader!!.setCanceledOnTouchOutside(false)
        return loader!!
    }
*/



    }

}