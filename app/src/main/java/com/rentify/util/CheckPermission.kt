package com.rentify.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class CheckPermission (context:Activity,val listener:PermissionListener){
    init {
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
                        showAlertOfPermissions(context)
                    }else{
                        listener.onPermissionGranted()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()


    }
    private fun showAlertOfPermissions(context: Activity) {

        AlertDialog.Builder(context)
            .setTitle("Permission Error")
            .setMessage("Require All Permissions to run the app properly. \nGo to settings and Allow All Permissions")
            .setCancelable(false)
            .setPositiveButton("Settings") { dialog, which ->
                goToSettings(context)
                dialog.dismiss()
            }
            .show()
    }

    private fun goToSettings(context:Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
        context.finish()
    }

    interface PermissionListener{
        fun onPermissionGranted()
    }

}