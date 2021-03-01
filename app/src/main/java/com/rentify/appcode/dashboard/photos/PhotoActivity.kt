package com.rentify.appcode.dashboard.photos

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rentify.R
import com.rentify.RentifyApp.Companion.getPhotoDB
import com.rentify.RentifyApp.Companion.session
import com.rentify.appcode.dashboard.propertyinfo.PropertyInfoActivity
import com.rentify.appcode.summary.SummaryActivity
import com.rentify.database.theme.SessionManager
import com.rentify.util.CheckPermission
import com.rentify.util.ConstUtils
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class PhotoActivity : AppCompatActivity(), View.OnClickListener,
    CheckPermission.PermissionListener {
    val summary: Boolean by lazy { intent.getBooleanExtra("summary", false) }
    val addinfo: Boolean by lazy { intent.getBooleanExtra("addinfo", false) }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        var myImageUri = ""
        fun createIntent(
            context: Context,
            forSummary: Boolean = true,
            addinfo: Boolean=false
        ): Intent {
            return Intent(context, PhotoActivity::class.java).apply {
                putExtra("summary", forSummary)
                putExtra("addinfo", addinfo)
            }
        }
    }

    var cover_Photo = ""
    var property_Photo1 = ""
    var property_Photo2 = ""
    var property_Photo3 = ""
    var property_Photo4 = ""
    var property_Photo5 = ""

    var photo_class=false
    var clickedView: View? = null
    var coverPhoto = Pair("", "")
    var propertyPhoto1 = Pair("", "")
    var propertyPhoto2 = Pair("", "")
    var propertyPhoto3 = Pair("", "")
    var propertyPhoto4 = Pair("", "")
    var propertyPhoto5 = Pair("", "")
    val displayMetrics by lazy {  DisplayMetrics() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        window!!.statusBarTheme()
        setContentView(R.layout.activity_photo)
        headerTool.headerTheme()
        ivCoverPhoto.setOnClickListener(this)
        ivPropertyPhoto1.setOnClickListener(this)
        ivPropertyPhoto2.setOnClickListener(this)
        ivPropertyPhoto3.setOnClickListener(this)
        ivPropertyPhoto4.setOnClickListener(this)
        ivPropertyPhoto5.setOnClickListener(this)
        btnBack.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnNext.bgTheme()
        btnBack.bgTheme()
        if (addinfo.not()) setEditInfo()
//        GlobalScope.launch {
//            launch(Dispatchers.IO) {
//                getFileData()
//            }
//        }
    }

    private fun setEditInfo() {
        cover_Photo= session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_COVER)!!
        property_Photo1= session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_1)!!
        property_Photo2= session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_2)!!
        property_Photo3= session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_3)!!
        property_Photo4= session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_4)!!
        property_Photo5= session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_5)!!
        setImage(ivCoverPhoto, session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_COVER)!!)
        setImage(ivPropertyPhoto1, session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_1)!!)
        setImage(ivPropertyPhoto2, session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_2)!!)
        setImage(ivPropertyPhoto3, session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_3)!!)
        setImage(ivPropertyPhoto4, session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_4)!!)
        setImage(ivPropertyPhoto5, session(this@PhotoActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_5)!!)
        lblCover.visibility=if (File(cover_Photo?:"").exists().not()) View.VISIBLE else View.GONE
        lblPropertyPhoto1.visibility=if (File(property_Photo1?:"").exists().not()) View.VISIBLE else View.GONE
        lblPropertyPhoto2.visibility=if (File(property_Photo2?:"").exists().not()) View.VISIBLE else View.GONE
        lblPropertyPhoto3.visibility=if (File(property_Photo3?:"").exists().not()) View.VISIBLE else View.GONE
        lblPropertyPhoto4.visibility=if (File(property_Photo4?:"").exists().not()) View.VISIBLE else View.GONE
        lblPropertyPhoto5.visibility=if (File(property_Photo5?:"").exists().not()) View.VISIBLE else View.GONE

    }

    suspend fun getFileData() {
        if (getPhotoDB(this@PhotoActivity).photoDao().getAll().isNotEmpty()) {
            val list = getPhotoDB(this@PhotoActivity).photoDao().getAll()
            list.forEachIndexed { index, fIleEntity ->
                when (index) {
                    0 -> {
                        coverPhoto = Pair(list[index].photoname!!, list[index].foldername!!)
                        setImage(ivCoverPhoto, list[index].photoname!!, list[index].foldername!!)
                    }
                    1 -> {
                        propertyPhoto1 = Pair(list[index].photoname!!, list[index].foldername!!)
                        setImage(
                            ivPropertyPhoto1,
                            list[index].photoname!!,
                            list[index].foldername!!
                        )
                    }
                    2 -> {
                        propertyPhoto2 = Pair(list[index].photoname!!, list[index].foldername!!)
                        setImage(
                            ivPropertyPhoto2,
                            list[index].photoname!!,
                            list[index].foldername!!
                        )
                    }
                    3 -> {
                        propertyPhoto3 = Pair(list[index].photoname!!, list[index].foldername!!)
                        setImage(
                            ivPropertyPhoto3,
                            list[index].photoname!!,
                            list[index].foldername!!
                        )
                    }
                    4 -> {
                        propertyPhoto4 = Pair(list[index].photoname!!, list[index].foldername!!)
                        setImage(
                            ivPropertyPhoto4,
                            list[index].photoname!!,
                            list[index].foldername!!
                        )
                    }
                    5 -> {
                        propertyPhoto5 = Pair(list[index].photoname!!, list[index].foldername!!)
                        setImage(
                            ivPropertyPhoto5,
                            list[index].photoname!!,
                            list[index].foldername!!
                        )
                    }
                }
            }
        }
    }

    fun setImage(view: ImageView, file: String, folder: String="") {
//        GlobalScope.launch {
//            launch(Dispatchers.Main) {
//                try {
//                    if (File(folder).exists()) {
        if (File(file).exists()) {
            view.setImageURI(Uri.fromFile(File(file)))
        }
//                    }
//                } catch (e: FileNotFoundException) {
//                }
//            }
//        }
    }

    override fun onClick(p0: View) {
        when (p0) {
            ivCoverPhoto, ivPropertyPhoto1, ivPropertyPhoto2, ivPropertyPhoto3, ivPropertyPhoto4, ivPropertyPhoto5 -> {
                clickedView = p0
                CheckPermission(this, this)
            }
            btnBack -> {
                if (photo_class==true){
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("GO_BACK_SUMMARY"))
                    finish()
                }else{
                    finish()
                }

            }
            btnNext -> {


//                GlobalScope.launch {
//                    launch(Dispatchers.IO) {
//                        if (getPhotoDB(this@PhotoActivity).photoDao().getAll().isEmpty()) {
//                            getPhotoDB(this@PhotoActivity).photoDao().insert(
//                                PhotoEntity(
//                                    uid = 0,
//                                    photoname = coverPhoto.first,
//                                    foldername = coverPhoto.second
//                                )
//                            )
//                            getPhotoDB(this@PhotoActivity).photoDao().insert(
//                                PhotoEntity(
//                                    uid = 1,
//                                    photoname = propertyPhoto1.first,
//                                    foldername = propertyPhoto1.second
//                                )
//                            )
//                            getPhotoDB(this@PhotoActivity).photoDao().insert(
//                                PhotoEntity(
//                                    uid = 2,
//                                    photoname = propertyPhoto2.first,
//                                    foldername = propertyPhoto2.second
//                                )
//                            )
//                            getPhotoDB(this@PhotoActivity).photoDao().insert(
//                                PhotoEntity(
//                                    uid = 3,
//                                    photoname = propertyPhoto3.first,
//                                    foldername = propertyPhoto3.second
//                                )
//                            )
//                            getPhotoDB(this@PhotoActivity).photoDao().insert(
//                                PhotoEntity(
//                                    uid = 4,
//                                    photoname = propertyPhoto4.first,
//                                    foldername = propertyPhoto4.second
//                                )
//                            )
//                            getPhotoDB(this@PhotoActivity).photoDao().insert(
//                                PhotoEntity(
//                                    uid = 5,
//                                    photoname = propertyPhoto5.first,
//                                    foldername = propertyPhoto5.second
//                                )
//                            )
//                        } else {
//                            val update = getPhotoDB(this@PhotoActivity).photoDao()
//                                .update(PhotoEntity(0, coverPhoto.first, coverPhoto.second))
//                            Log.e("UPDATE", update.toString())
//                            val update1 = getPhotoDB(this@PhotoActivity).photoDao()
//                                .update(PhotoEntity(1, propertyPhoto1.first, propertyPhoto1.second))
//                            Log.e("UPDATE1", update1.toString())
//                            val update2 = getPhotoDB(this@PhotoActivity).photoDao()
//                                .update(PhotoEntity(2, propertyPhoto2.first, propertyPhoto2.second))
//                            Log.e("UPDATE2", update2.toString())
//                            val update3 = getPhotoDB(this@PhotoActivity).photoDao()
//                                .update(PhotoEntity(3, propertyPhoto3.first, propertyPhoto3.second))
//                            Log.e("UPDATE3", update3.toString())
//                            val update4 = getPhotoDB(this@PhotoActivity).photoDao()
//                                .update(PhotoEntity(4, propertyPhoto4.first, propertyPhoto4.second))
//                            Log.e("UPDATE4", update4.toString())
//                            val update5 = getPhotoDB(this@PhotoActivity).photoDao()
//                                .update(PhotoEntity(5, propertyPhoto5.first, propertyPhoto5.second))
//                            Log.e("UPDATE5", update5.toString())
//                        }
//                    }
//                }

                session(this@PhotoActivity).apply {
                    savePrefValue(SessionManager.PROPERTY_COVER, cover_Photo)
                    savePrefValue(SessionManager.PROPERTY_IMAGE_1, property_Photo1)
                    savePrefValue(SessionManager.PROPERTY_IMAGE_2, property_Photo2)
                    savePrefValue(SessionManager.PROPERTY_IMAGE_3, property_Photo3)
                    savePrefValue(SessionManager.PROPERTY_IMAGE_4, property_Photo4)
                    savePrefValue(SessionManager.PROPERTY_IMAGE_5, property_Photo5)
                }

                if (intent.hasExtra("page")){
                    showDoneDialog(msg = getString(R.string.file_save_success)) {
                        if (summary) {
                            startActivity(
                                Intent(this@PhotoActivity,SummaryActivity::class.java)
                            )
                            finish()
                        } else {
                            onBackPressed()
                        }
                    }
                }else{
                    if (summary) {
                        startActivity(
                            Intent(this@PhotoActivity,SummaryActivity::class.java))
                    } else {
                        onBackPressed()
                    }
                }
            }
        }
    }

    val mBuilder: Dialog by lazy { Dialog(this) }
    fun showImagePop() {
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleCamera)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakePictureIntent()
            }
        mBuilder.findViewById<TextView>(R.id.titleGallery)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent()
            }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(this@PhotoActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "$packageName.provider",
                        it
                    )
                    myImageUri = photoURI.toString()
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, ConstUtils.REQUEST_TAKE_PHOTO)
                }
            }

        }
    }

    private fun dispatchTakeGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, ConstUtils.REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
//                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setMinCropWindowSize(1400,1400)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri)
//                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(2, 1)
                    .setMinCropWindowSize(1400,1400)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    private fun saveCaptureImageResults(data: Uri) {
        try {
            val file = File(data.path!!)
            val compressedImageFile = Compressor(this)
                .setMaxHeight(1400).setMaxWidth(1400)
                .setQuality(90)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(file)

            when (clickedView) {
                ivCoverPhoto -> cover_Photo = compressedImageFile.path
                ivPropertyPhoto1 -> property_Photo1 = compressedImageFile.path
                ivPropertyPhoto2 -> property_Photo2 = compressedImageFile.path
                ivPropertyPhoto3 -> property_Photo3 = compressedImageFile.path
                ivPropertyPhoto4 -> property_Photo4 = compressedImageFile.path
                ivPropertyPhoto5 -> property_Photo5 = compressedImageFile.path
            }

/*
//            when (clickedView) {
//                ivCoverPhoto -> {
////                    coverPhoto = Pair(
////                        if (compressedImageFile.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.path else compressedImageFile.absolutePath,
////                        if (compressedImageFile.parentFile!!.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.parentFile!!.path else compressedImageFile.parentFile!!.absolutePath
////                    )
//                }
//                ivPropertyPhoto1 -> {
////                    propertyPhoto1 = Pair(
////                        if (compressedImageFile.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.path else compressedImageFile.absolutePath,
////                        if (compressedImageFile.parentFile!!.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.parentFile!!.path else compressedImageFile.parentFile!!.absolutePath
////                    )
//                }
//                ivPropertyPhoto2 -> {
////                    propertyPhoto2 = Pair(
////                        if (compressedImageFile.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.path else compressedImageFile.absolutePath,
////                        if (compressedImageFile.parentFile!!.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.parentFile!!.path else compressedImageFile.parentFile!!.absolutePath
////                    )
//                }
//                ivPropertyPhoto3 -> {
////                    propertyPhoto3 = Pair(
////                        if (compressedImageFile.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.path else compressedImageFile.absolutePath,
////                        if (compressedImageFile.parentFile!!.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.parentFile!!.path else compressedImageFile.parentFile!!.absolutePath
////                    )
//                }
//                ivPropertyPhoto4 -> {
////                    propertyPhoto4 = Pair(
////                        if (compressedImageFile.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.path else compressedImageFile.absolutePath,
////                        if (compressedImageFile.parentFile!!.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.parentFile!!.path else compressedImageFile.parentFile!!.absolutePath
////                    )
//                }
//                ivPropertyPhoto5 -> {
////                    propertyPhoto5 = Pair(
////                        if (compressedImageFile.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.path else compressedImageFile.absolutePath,
////                        if (compressedImageFile.parentFile!!.path.isEmpty()
////                                .not()
////                        ) compressedImageFile.parentFile!!.path else compressedImageFile.parentFile!!.absolutePath
////                    )
//                }
//            }
*/
            (clickedView!! as ImageView).setImageURI(Uri.fromFile(compressedImageFile))
            lblCover.visibility=if (File(cover_Photo?:"").exists().not()) View.VISIBLE else View.GONE
            lblPropertyPhoto1.visibility=if (File(property_Photo1?:"").exists().not()) View.VISIBLE else View.GONE
            lblPropertyPhoto2.visibility=if (File(property_Photo2?:"").exists().not()) View.VISIBLE else View.GONE
            lblPropertyPhoto3.visibility=if (File(property_Photo3?:"").exists().not()) View.VISIBLE else View.GONE
            lblPropertyPhoto4.visibility=if (File(property_Photo4?:"").exists().not()) View.VISIBLE else View.GONE
            lblPropertyPhoto5.visibility=if (File(property_Photo5?:"").exists().not()) View.VISIBLE else View.GONE

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPermissionGranted() {
        showImagePop()
    }


    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broardReceiver,
            IntentFilter("GO_BACK")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broardReceiver)
    }

    val broardReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            photo_class=true
            setEditInfo()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (photo_class==true){
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("GO_BACK_SUMMARY"))
            finish()
        }else{
            finish()
        }
    }
}