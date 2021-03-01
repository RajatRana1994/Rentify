package com.rentify.appcode.dashboard.files

import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.google.gson.Gson
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.RentifyApp.Companion.session
import com.rentify.appcode.input.InputActivity
import com.rentify.database.theme.SessionManager
import com.rentify.local.PDFDataModel
import com.rentify.util.CheckPermission
import com.rentify.util.ConstUtils.Companion.appFolderPath
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.statusBarTheme
import kotlinx.android.synthetic.main.activity_files.*
import java.io.File


class SubFilesActivity : AppCompatActivity(), CheckPermission.PermissionListener,
    View.OnClickListener {
    
    val folder_path by lazy { intent.getStringExtra("folder_path")?:"" }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)
        window!!.statusBarTheme()
        headerTool.headerTheme()
        btnBack.bgTheme()
        titleHeader.text=getString(R.string.folder)
        btnBack.setOnClickListener { onBackPressed() }
        ivAddFolder.visibility=View.GONE
        ivAddFolder.setOnClickListener(this)
        CheckPermission(this, this)

    }

    private fun getLocalFiles() {
        llInflater.removeAllViews()
        llFolderInflater.removeAllViews()
        if (File(folder_path)!!.exists()) {
            val files = File(folder_path)!!.listFiles()
            files.forEach {
                if (it.isDirectory.not()) showFiles(it) else showFolders(it)
            }
        }
    }

    override fun onPermissionGranted() {
        getLocalFiles()
    }

    private fun showFiles(file: File) {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.row_file, llInflater, false)
        view.findViewById<TextView>(R.id.tvFolder).setOnSingleClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }
        view.findViewById<ImageView>(R.id.ivMsg).setOnSingleClickListener {
            composeMail(file)
        }
        view.findViewById<ImageView>(R.id.ivEdit).setOnSingleClickListener {
            checkToEditPdf(file)
        }
        view.findViewById<ImageView>(R.id.ivMove).visibility=View.GONE
        view.findViewById<ImageView>(R.id.ivMove).setOnSingleClickListener {
            if (llFolderInflater.childCount > 0) {
                val mBuilder = AlertDialog.Builder(this@SubFilesActivity,R.style.ThemeOverlay_MaterialComponents_Light)
                mBuilder.setTitle("Move to")
                val listItems = arrayOfNulls<String>(llFolderInflater.childCount)
                llFolderInflater.children.forEachIndexed { index, view ->
                    listItems[index] = view.findViewById<TextView>(R.id.tvFolder).text.toString()
                }
                mBuilder.setSingleChoiceItems(
                    listItems,
                    -1
                ) { dialog, which ->
                    dialog.cancel()
                    moveFile(
                        file,
                        File(File(folder_path)!!!!.path + File.separator + listItems[which] + File.separator + file.name)
                    )
                }
                // Set the neutral/cancel button click listener
                mBuilder.setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                    // Do something when click the neutral button
                    dialog.cancel()
                }

                val mDialog = mBuilder.create()
                mDialog.show()
            }
        }
        view.findViewById<TextView>(R.id.tvFolder).text =
            "${llInflater.childCount + 1}). ${file.name}"
        llInflater.addView(view)

    }

    private fun checkToEditPdf(file: File) {
        session(this@SubFilesActivity).pref.all.entries.forEach {
            if (file.name.equals(it.key)){
                val pdfData= Gson().fromJson<PDFDataModel>(it.value!! as String,PDFDataModel::class.java)!!
//                session(this@FilesActivity).apply {
//                    savePrefValue(SessionManager.RENTAL_INCREASES,pdfData.dafaultData!!.RENTAL_INCREASES)
//                    savePrefValue(SessionManager.APPRECIATION_GROWTH,pdfData.dafaultData!!.APPRECIATION_GROWTH)
//                    savePrefValue(SessionManager.PRINCIPAL_PAYMENT,pdfData.dafaultData!!.PRINCIPAL_PAYMENT)
//                    savePrefValue(SessionManager.IMPOUND_ACCOUNT,pdfData.dafaultData!!.IMPOUND_ACCOUNT)
//                    savePrefValue(SessionManager.VACANCY_FACTOR,pdfData.dafaultData!!.VACANCY_FACTOR)
//                    savePrefValue(SessionManager.LOAN_INTREST,pdfData.dafaultData!!.LOAN_INTREST)
//                    savePrefValue(SessionManager.PROPERTY_TAX,pdfData.dafaultData!!.PROPERTY_TAX)
//                    savePrefValue(SessionManager.HOMEOWNERS_INSURANCE,pdfData.dafaultData!!.HOMEOWNERS_INSURANCE)
//                    savePrefValue(SessionManager.MAINTENANCE_EXP,pdfData.dafaultData!!.MAINTENANCE_EXP)
//                }
                startActivity(Intent(this@SubFilesActivity,InputActivity::class.java).putExtra("pdf_data",it.value!! as String))
            }
        }
    }

    private fun showFolders(folder: File) {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.row_folder, llFolderInflater, false)
        view.findViewById<ImageView>(R.id.ivFolder).setOnSingleClickListener {
        }
        view.findViewById<TextView>(R.id.tvFolder).text = folder.name

        llFolderInflater.addView(view)

    }

    override fun onClick(p0: View?) {
        showAddFolderDialog()
    }


    fun showAddFolderDialog() {
        val builder = AlertDialog.Builder(this)
        val viewDialog = layoutInflater.inflate(R.layout.dialog_add_folder, null)
        builder.setView(viewDialog)
        val alertDialog = builder.create()

        val edtFolderName = viewDialog.findViewById<EditText>(R.id.edtFolderName)
        viewDialog.findViewById<TextView>(R.id.btnCancel).setOnClickListener {
            alertDialog!!.cancel()
        }
        viewDialog.findViewById<TextView>(R.id.btnConfirm).setOnClickListener {
            if (edtFolderName.text.isEmpty()) Toast.makeText(
                this@SubFilesActivity,
                getString(R.string.enter_folder_name),
                Toast.LENGTH_SHORT
            ).show()
            else {
                alertDialog!!.cancel()
                createFolder(edtFolderName.text.toString())
            }
        }
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()

        alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog!!.window?.getAttributes())
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        layoutParams.width = dialogWindowWidth
        alertDialog!!.window?.attributes = layoutParams
    }

    private fun createFolder(foldername: String) {
        if (File(File(folder_path)!!!!.path + File.separator + foldername).exists().not()) {
            val created =
                File(File(folder_path)!!!!.path + File.separator + foldername)!!.mkdir()
            Toast.makeText(
                this@SubFilesActivity,
                if (created) "Created" else "Fail",
                Toast.LENGTH_SHORT
            ).show()
            getLocalFiles()
        } else Toast.makeText(this@SubFilesActivity, "Already Exist!", Toast.LENGTH_SHORT).show()
    }

    fun moveFile(source: File, target: File) {
        val moved = source.renameTo(target)
        Toast.makeText(
            this@SubFilesActivity,
            if (moved) "Moved File" else "Failed!",
            Toast.LENGTH_SHORT
        ).show()

        getLocalFiles()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Files.move(
//                Paths.get(source.path),
//                Paths.get(target.path),
//                StandardCopyOption.ATOMIC_MOVE
//            )
//            getLocalFiles()
//        } else {
//            try {
//                source.copyTo(target,true)
//                source.delete()
//            }catch (e:Exception){}
//        }
    }

    private fun composeMail(file: File) {
        val path = Uri.fromFile(file)
        var isFound = false
        val shareIntent = Intent(Intent.ACTION_SEND);
        shareIntent.type = "*/*";
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent by Android device")
        shareIntent.putExtra(Intent.EXTRA_STREAM, path)
        try {
            val pm = packageManager;
            val activityList = pm.queryIntentActivities(shareIntent, 0);
            for (app in activityList) {
                if ((app.activityInfo.name).contains("google.android.gm") || (app.activityInfo.name).contains(
                        "gmail"
                    )
                ) {
                    isFound = true
                    val activity = app.activityInfo
                    val name = ComponentName(activity.applicationInfo.packageName, activity.name)
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shareIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    shareIntent.component = name
                    startActivity(shareIntent)
                }
            }
            if (!isFound) {
                try {
                    val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntent(arrayOfNulls<Parcelable>(shareIntent.size())))
                    startActivity(chooserIntent)
                } catch (e: java.lang.Exception) {
                    Toast.makeText(this@SubFilesActivity, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            try {
                val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
                startActivity(chooserIntent)
            } catch (e: java.lang.Exception) {
                Toast.makeText(this@SubFilesActivity, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

}