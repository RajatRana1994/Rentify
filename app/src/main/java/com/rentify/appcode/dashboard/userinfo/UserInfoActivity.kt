package com.rentify.appcode.dashboard.userinfo

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.rentify.R
import com.rentify.RentifyApp.Companion.session
import com.rentify.appcode.dashboard.propertyinfo.PropertyInfoActivity
import com.rentify.appcode.splash.GetStartedActivity
import com.rentify.database.theme.SessionManager
import com.rentify.util.CheckPermission
import com.rentify.util.ConstUtils
import com.rentify.util.Extensions.Companion.addPhoneFormat
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.redError
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.Extensions.Companion.viewTheme
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_user_info.*
import java.io.File
import java.util.*


class UserInfoActivity : AppCompatActivity(), CheckPermission.PermissionListener, TextWatcher {
    private var imageUri: Uri? = null

    private val displayMetrics by lazy { DisplayMetrics() }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        var logo_url = ""
        var new_logo_url: Pair<String, String>? = null
        fun createIntent(
            context: Context, isBack: Boolean = true, addinfo: Boolean = true
        ): Intent {
            return Intent(context, UserInfoActivity::class.java).apply {
                putExtra("isBack", isBack)
                putExtra("addinfo", addinfo)
            }
        }
    }


    private val isBackExtra by lazy { intent.getBooleanExtra("isBack", true) }
    private val addinfo by lazy { intent.getBooleanExtra("addinfo", false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.statusBarTheme()
        setContentView(R.layout.activity_user_info)
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        headerTool.headerTheme()
        fetchUserInfo()
        btnBack.visibility = if (isBackExtra) View.VISIBLE else View.INVISIBLE
        btnBack.setOnClickListener {
            onBackPressed()
        }
        edtPhoneNum.addPhoneFormat()
        edtAddress.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }
        edtUserName.addTextChangedListener(this)
        edtCompanyName.addTextChangedListener(this)
        edtEmail.addTextChangedListener(this)
        edtPhoneNum.addTextChangedListener(this)
        edtAddress.addTextChangedListener(this)
        edtCity.addTextChangedListener(this)
        edtState.addTextChangedListener(this)
        edtPostCode.addTextChangedListener(this)
        btnNext.setOnClickListener {
            if (edtUserName.text.isEmpty()) {
                setError(edtUserName)
            } else if (edtCompanyName.text.isEmpty()) {
                setError(edtCompanyName)
            } else if (edtEmail.text.isEmpty()) {
                setError(edtEmail)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()) {
                setError(edtEmail)
            } else if (edtPhoneNum.text.isEmpty()) {
                setError(edtPhoneNum)
            } else if (edtAddress.text.isEmpty()) {
                setError(edtAddress)
            } else if (edtCity.text.isEmpty()) {
                setError(edtCity)
            } else if (edtState.text.isEmpty()) {
                setError(edtState)
            } else if (edtPostCode.text.isEmpty()) {
                setError(edtPostCode)
            } else {
                session(this)
                    .savePrefValue(SessionManager.USER_NAME_USERINFO, edtUserName.text.toString())
                session(this).savePrefValue(
                    SessionManager.COMPANY_NAME_USERINFO,
                    edtCompanyName.text.toString()
                )
                session(this)
                    .savePrefValue(SessionManager.EMAIL_USERINFO, edtEmail.text.toString())
                session(this)
                    .savePrefValue(SessionManager.PHONE_USERINFO, edtPhoneNum.text.toString())
                session(this)
                    .savePrefValue(SessionManager.ADDRESS_USERINFO, edtAddress.text.toString())
                session(this)
                    .savePrefValue(SessionManager.CITY_USERINFO, edtCity.text.toString())
                session(this)
                    .savePrefValue(SessionManager.STATE_USERINFO, edtState.text.toString())
                session(this)
                    .savePrefValue(SessionManager.POSTCODE_USERINFO, edtPostCode.text.toString())
                if (new_logo_url != null)
                    session(this).savePrefValue(SessionManager.LOGO_USERINFO, new_logo_url!!.first)

                showDoneDialog(msg = getString(R.string.userinfo_save_success)) {
                    if (isBackExtra) {
                        onBackPressed()
                    } else {
                        if (addinfo.not()) {
                            startActivity(
                                PropertyInfoActivity.createIntent(
                                    this@UserInfoActivity,
                                    true,
                                    addinfo
                                )
                            )
                        } else
                            startActivity(
                                Intent(
                                    this@UserInfoActivity, GetStartedActivity::class.java
                                )
                            )
                    }
                }
            }
        }
        ivCoverPhoto.setOnClickListener {
            CheckPermission(this, this)
        }

    }

    private fun fetchUserInfo() {
        edtUserName.setText(
            session(this).getPrefValue(SessionManager.USER_NAME_USERINFO)
        )
        edtCompanyName.setText(
            session(this).getPrefValue(SessionManager.COMPANY_NAME_USERINFO)
        )
        edtEmail.setText(session(this).getPrefValue(SessionManager.EMAIL_USERINFO))
        edtPhoneNum.setText(session(this).getPrefValue(SessionManager.PHONE_USERINFO))
        edtAddress.setText(session(this).getPrefValue(SessionManager.ADDRESS_USERINFO))
        edtCity.setText(session(this).getPrefValue(SessionManager.CITY_USERINFO))
        edtState.setText(session(this).getPrefValue(SessionManager.STATE_USERINFO))
        edtPostCode.setText(session(this).getPrefValue(SessionManager.POSTCODE_USERINFO))

        if (session(this).getPrefValue(SessionManager.LOGO_USERINFO)!!.isNotEmpty()) {
            if (File(session(this).getPrefValue(SessionManager.LOGO_USERINFO)!!).exists()) {
                ivCoverPhoto.setImageURI(Uri.parse(session(this).getPrefValue(SessionManager.LOGO_USERINFO)!!))
                lblCoverPhoto.visibility = View.GONE
            }
        }
    }

    private fun setError(view: EditText) {
        edtUserName.hint =
            if (edtUserName == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtCompanyName.hint =
            if (edtCompanyName == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtEmail.hint =
            if (edtEmail == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtPhoneNum.hint =
            if (edtPhoneNum == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtAddress.hint =
            if (edtAddress == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtCity.hint =
            if (edtCity == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtState.hint =
            if (edtState == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtPostCode.hint =
            if (edtPostCode == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null

        if (view.hint.isNullOrEmpty().not()) view.redError()
//            view.background=ContextCompat.getDrawable(this@UserInfoActivity,R.drawable.edt_rounded_error_drawable)
//        }


    }

    override fun onPermissionGranted() {
        showImagePop()
    }

    private val mBuilder: Dialog by lazy {
        Dialog(this).apply {
            setContentView(R.layout.camera_dialog);
            window?.apply {
                attributes.windowAnimations = R.style.DialogAnimation;
                setGravity(Gravity.BOTTOM)
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            findViewById<TextView>(R.id.titleCamera).setOnClickListener {
                mBuilder.dismiss()
                dispatchTakePictureIntent()
            }
            findViewById<TextView>(R.id.titleGallery).setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent()
            }
            findViewById<TextView>(R.id.titleCancel).setOnClickListener { mBuilder.dismiss() }

        }
    }

    private fun showImagePop() {
        if (!mBuilder.isShowing) {
            mBuilder.show();
        }
    }

    private fun dispatchTakePictureIntent() {
        imageUri = null
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, getString(R.string.app_name))
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                imageUri =
                    contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, ConstUtils.REQUEST_TAKE_PHOTO)
            }
        } catch (e: Exception) {
            Log.e("camera error", e.localizedMessage.toString())
            e.printStackTrace()
        }

    }

    private fun dispatchTakeGalleryIntent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/jpg", "image/jpeg"))
        startActivityForResult(
            Intent.createChooser(intent, "Choose Image From"), ConstUtils.REQUEST_IMAGE_GET
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    try {
                        val place: Place = Autocomplete.getPlaceFromIntent(data)
                        setPlaceData(place)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            ConstUtils.REQUEST_TAKE_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    CropImage.activity(imageUri)
                        .setMinCropWindowSize(1400, 1400)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                        .setGuidelinesColor(android.R.color.transparent).start(this)
                } else {
                    /* shohw error message*/
                    Log.e("camera error", "Handle camera error")
                }
            }
            ConstUtils.REQUEST_IMAGE_GET -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { img ->
                        CropImage.activity(img)
                            .setMinCropWindowSize(1400, 1400)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                            .setGuidelinesColor(android.R.color.transparent).start(this)

                    }
                } else {
                    /* show error here */
                    Log.e("gallery error", "Handle gallery error")

                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    Log.e("Crop error", "Handle crop error")

                }
            }
        }

    }

    private fun setPlaceData(placeData: Place?) {
        try {
            if (placeData != null) {
                edtAddress.setText("")
                edtCity.setText("")
                edtState.setText("")
                edtPostCode.setText("")
                if (placeData.name?.toLowerCase()?.isNotEmpty() == true) {
                    edtAddress.setText(placeData.getName())
                    edtAddress.setSelection(edtAddress.getText().length)
                } else edtAddress.redError()
                for (i in 0 until placeData.addressComponents!!.asList().size) {
                    val place: AddressComponent =
                        placeData.getAddressComponents()!!.asList().get(i)

//                    if (place.types.contains("street_number")) {
//                        edtAddress.setText(place.name)
//                        edtAddress.setSelection(edtAddress.getText().length)
//                    }
//                    if ( /*place.getTypes().contains("neighborhood")||*/place.types
//                            .contains("route")
//                    ) {
//                        edtAddress.setText("${edtAddress.getText()} ${place.name}")
//                    }

                    if (edtCity.text.toString().trim().isEmpty()) {
                        if (place.types.contains("neighborhood") && place.types.contains("political")) {
                            edtCity.apply {
                                hint = ""
                                setText(place.name)
                            }
                        }
                    }

                    if (edtCity.text.toString().trim().isEmpty()) {
                        if (place.types.contains("locality") && place.types.contains("political")) {
                            edtCity.apply {
                                hint = ""
                                setText(place.name)
                            }
                        }
                    }

                    if (place.types.contains("administrative_area_level_1")) {
                        edtState.apply {
                            hint = ""
                            setText(place.name)
                        }
                    }
                    if (place.types.contains("postal_code")) {
                        edtPostCode.apply {
                            hint = ""
                            setText(place.name)
                        }
                    }
                }
                if (edtCity.text.toString().isEmpty())
                    edtCity.apply {
                        hint = getString(R.string.this_field_req)
                        redError()
                    }
                if (edtState.text.toString().isEmpty())
                    edtState.apply {
                        hint = getString(R.string.this_field_req)
                        redError()
                    }
                if (edtPostCode.text.toString().isEmpty())
                    edtPostCode.apply {
                        hint = getString(R.string.this_field_req)
                        redError()
                    }
            }
        } catch (e: java.lang.Exception) {
            Log.e("Exce......", e.toString())
        }

    }

    private fun saveCaptureImageResults(data: Uri) {
        try {
            val file = File(data.path!!)
            val compressedImageFile = Compressor(this)
//                .setMaxHeight(800).setMaxWidth(800)
                .setQuality(90)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(file)
            ivCoverPhoto.setImageURI(Uri.fromFile(compressedImageFile))
            lblCoverPhoto.visibility = View.GONE
            new_logo_url = Pair(
                if (compressedImageFile.path.isEmpty()
                        .not()
                ) compressedImageFile.path else compressedImageFile.absolutePath,
                if (compressedImageFile.parentFile!!.path.isEmpty()
                        .not()
                ) compressedImageFile.parentFile!!.path else compressedImageFile.parentFile!!.absolutePath
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun openPlaceDialog() {
        val fields = Arrays.asList(
            Place.Field.PHONE_NUMBER,
            Place.Field.BUSINESS_STATUS,
            Place.Field.ADDRESS,
            Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ID
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this@UserInfoActivity)
        startActivityForResult(intent, ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onBackPressed() = if (isBackExtra) super.onBackPressed() else finishAffinity()

    override fun onResume() {
        super.onResume()
        refreshTheme()
    }

    private fun refreshTheme() {
        edtUserName.viewTheme()
        edtCompanyName.viewTheme()
        edtEmail.viewTheme()
        edtPhoneNum.viewTheme()
        edtAddress.viewTheme()
        edtCity.viewTheme()
        edtState.viewTheme()
        edtPostCode.viewTheme()
        btnNext.bgTheme()
        btnBack.bgTheme()
    }

    override fun afterTextChanged(p0: Editable?) {
        refreshTheme()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
}