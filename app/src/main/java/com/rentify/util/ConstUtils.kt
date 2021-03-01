package com.rentify.util

import android.content.Context
import android.os.Environment
import com.rentify.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ConstUtils {
    companion object{
        fun Context.appFolderPath(): File? {
             (this.getExternalFilesDir("${Environment.DIRECTORY_DOWNLOADS}${File.separator}Rentify${File.separator}")).apply {
                 if (this!!.exists().not())
                     this.mkdir()
                 return this
             }
//             (Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOCUMENTS}${File.separator}Rentify${File.separator}")).apply {
//                 if (this!!.exists().not())
//                     this.mkdir()
//                 return this
//             }
//            return Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOCUMENTS}${File.separator}Rentify${File.separator}")
        }
        val time=android.text.format.DateFormat.format("yyyyMMddhhmmss", Date());

        fun createImageFile(context: Context): File {
            // Create an image file eventName
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = context.getExternalFilesDir("${Environment.DIRECTORY_PICTURES}${File.separator}.${context.getString(
                R.string.app_name)}")
            val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
            return image
        }



        val REQUEST_TAKE_PHOTO=3210
        val REQUEST_IMAGE_GET=1032
        val PLACE_AUTOCOMPLETE_REQUEST_CODE=1121



        ///
        val Purchase_Price="Purchase Price"
        val Property_Value_after_renovation="Property Value after renovation"
        val Market_Rent="Market Rent"
        val Rental_Deposit="Rental Deposit"
        val Loan_Down_Payment="Loan Down Payment"
        val Loan_Cost="Loan Cost"
        val Improvements="Improvements"
        val Annual_Rental_Increase="Annual Rental Increase(%)"
        val Area_Appreciation_Growth="Area Appreciation Growth(%)"
        val Principal_Payments="Principal Payments(%)"
        val Impound_Account="Impound Account(%)"
        val Vacancy_Factor="Vacancy Factor(%)"
        val Loan_Interest="Loan Interest(%)"
        val Property_Tax="Property Tax(%)"
        val Homeowners_Insurance="Homeowners Insurance(%)"
        val Maintenance_Expense="Maintenance Expense(%)"
        val Annual_Management="Annual Management($)"
        val Annual_other_expenses="Annual other expenses($)"
        val Annual_Expenses="Annual Expenses"
        val Monthly_cash_requirement="Monthly cash requirement"
        val Annual_Income="Annual Income"
        val Cash_on_Cash_return="Cash on Cash return"
        val Percentage_Cash_on_return="Percentage Cash on return"
        val Annual_Net_Operating_Income="Annual Net Operating Income"
    }
}