package com.rentify.appcode.dashboard.propertyinfo

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.appcode.dashboard.photos.PhotoActivity
import com.rentify.appcode.summary.SummaryActivity
import com.rentify.database.theme.SessionManager
import com.rentify.util.ConstUtils
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.redError
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.Extensions.Companion.viewTheme
import kotlinx.android.synthetic.main.activity_property_info.*
import kotlinx.android.synthetic.main.activity_property_info.btnBack
import kotlinx.android.synthetic.main.activity_property_info.btnNext
import kotlinx.android.synthetic.main.activity_property_info.edtCity
import kotlinx.android.synthetic.main.activity_property_info.edtPostCode
import kotlinx.android.synthetic.main.activity_property_info.edtState
import kotlinx.android.synthetic.main.activity_property_info.headerTool
import kotlinx.android.synthetic.main.activity_user_info.*
import java.util.*

class PropertyInfoActivity : AppCompatActivity() {
    val summary: Boolean by lazy { intent.getBooleanExtra("summary", false) }
    val addinfo: Boolean by lazy { intent.getBooleanExtra("addinfo", false) }

    var summary_class=false
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.statusBarTheme()
        setContentView(R.layout.activity_property_info)
        headerTool.headerTheme()
//        if (intent.hasExtra("addinfo")){
//            addinfo=intent.getBooleanExtra("addinfo")
//        }
        if (addinfo.not()) fetchPropertyInfo()
        btnNext.bgTheme()
        btnBack.bgTheme()


        edtPropertyAddress.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
        btnNext.setOnClickListener {
            performNextClick()
        }
    }

    private fun performNextClick() {
        if (edtPropertyAddress.text.isEmpty()) {
            setError(edtPropertyAddress)
        } else if (edtSquareFeet.text.isEmpty()) {
            setError(edtSquareFeet)
        } else if (edtBedRoom.text.isEmpty()) {
            setError(edtBedRoom)
        } else if (edtBathRoom.text.isEmpty()) {
            setError(edtBathRoom)
        } else if (edtLotSize.text.isEmpty()) {
            setError(edtLotSize)
        } else if (edtCity.text.isEmpty()) {
            setError(edtCity)
        } else if (edtState.text.isEmpty()) {
            setError(edtState)
        } else if (edtPostCode.text.isEmpty()) {
            setError(edtPostCode)
        } else {
            RentifyApp.session(this).savePrefValue(
                SessionManager.ADDRESS_PROPERTY_INFO,
                edtPropertyAddress.text.toString()
            )
            RentifyApp.session(this).savePrefValue(
                SessionManager.SQUARE_FEET_PROPERTY_INFO,
                edtSquareFeet.text.toString()
            )
            RentifyApp.session(this).savePrefValue(
                SessionManager.BED_ROOMS_PROPERTY_INFO,
                edtBedRoom.text.toString()
            )
            RentifyApp.session(this).savePrefValue(
                SessionManager.BATH_ROOMS_PROPERTY_INFO,
                edtBathRoom.text.toString()
            )
            RentifyApp.session(this).savePrefValue(
                SessionManager.LOT_SIZE_PROPERTY_INFO,
                edtLotSize.text.toString()
            )
            RentifyApp.session(this)
                .savePrefValue(SessionManager.CITY_PROPERTY_INFO, edtCity.text.toString())
            RentifyApp.session(this)
                .savePrefValue(SessionManager.STATE_PROPERTY_INFO, edtState.text.toString())
            RentifyApp.session(this).savePrefValue(
                SessionManager.POSTCODE_PROPERTY_INFO,
                edtPostCode.text.toString()
            )



            if (intent.hasExtra("page")){
                showDoneDialog(msg = getString(R.string.property_info_save_success)) {
                if (summary){
                    startActivity(PhotoActivity.createIntent(this@PropertyInfoActivity,summary,addinfo))
                    finish()
                }else onBackPressed()
            }
            }else{
                if (summary){
                    startActivity(PhotoActivity.createIntent(this@PropertyInfoActivity,summary,addinfo))
                }else onBackPressed()
            }
        }
    }

    private fun fetchPropertyInfo() {
//        edtPropertyAddress.setText(RentifyApp.session(this).getPrefValue(SessionManager.ADDRESS_PROPERTY_INFO))
//        edtSquareFeet.setText(RentifyApp.session(this).getPrefValue(SessionManager.SQUARE_FEET_PROPERTY_INFO))
//        edtBedRoom.setText(RentifyApp.session(this).getPrefValue(SessionManager.BED_ROOMS_PROPERTY_INFO))
//        edtBathRoom.setText(RentifyApp.session(this).getPrefValue(SessionManager.BATH_ROOMS_PROPERTY_INFO))
//        edtLotSize.setText(RentifyApp.session(this).getPrefValue(SessionManager.LOT_SIZE_PROPERTY_INFO))
//        edtCity.setText(RentifyApp.session(this).getPrefValue(SessionManager.CITY_PROPERTY_INFO))
//        edtState.setText(RentifyApp.session(this).getPrefValue(SessionManager.STATE_PROPERTY_INFO))
//        edtPostCode.setText(RentifyApp.session(this).getPrefValue(SessionManager.POSTCODE_PROPERTY_INFO))

        edtPropertyAddress.setText(RentifyApp.session(this).getPrefValue(SessionManager.ADDRESS_PROPERTY_INFO))
        edtSquareFeet.setText(RentifyApp.session(this).getPrefValue(SessionManager.SQUARE_FEET_PROPERTY_INFO))
        edtBedRoom.setText(RentifyApp.session(this).getPrefValue(SessionManager.BED_ROOMS_PROPERTY_INFO))
        edtBathRoom.setText(RentifyApp.session(this).getPrefValue(SessionManager.BATH_ROOMS_PROPERTY_INFO))
        edtLotSize.setText(RentifyApp.session(this).getPrefValue(SessionManager.LOT_SIZE_PROPERTY_INFO))
        edtCity.setText(RentifyApp.session(this).getPrefValue(SessionManager.CITY_PROPERTY_INFO))
        edtState.setText(RentifyApp.session(this).getPrefValue(SessionManager.STATE_PROPERTY_INFO))
        edtPostCode.setText(RentifyApp.session(this).getPrefValue(SessionManager.POSTCODE_PROPERTY_INFO))
    }


    fun setError(view: EditText) {
        edtPropertyAddress.hint =
            if (edtPropertyAddress == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtSquareFeet.hint =
            if (edtSquareFeet == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtBedRoom.hint =
            if (edtBedRoom == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtBathRoom.hint =
            if (edtBathRoom == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtLotSize.hint =
            if (edtLotSize == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtCity.hint =
            if (edtCity == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtState.hint =
            if (edtState == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null
        edtPostCode.hint =
            if (edtPostCode == view) if (view.text.isEmpty()) getString(R.string.this_field_req) else null else null

        if (view.hint.isNullOrEmpty().not()) view.redError()
    }


    fun openPlaceDialog() {
        val fields = Arrays.asList(
            Place.Field.PHONE_NUMBER,
            Place.Field.BUSINESS_STATUS,
            Place.Field.ADDRESS,
            Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ID
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this@PropertyInfoActivity)
        startActivityForResult(intent, ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (edtPropertyAddress.hasFocus()) edtPropertyAddress.clearFocus()
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    setPlaceData(place)
                } catch (e: java.lang.Exception) {
                }
            }
        }
    }


    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                edtPropertyAddress.setText("")
                edtCity.setText("")
                edtState.setText("")
                edtPostCode.setText("")
                if (!placeData.getName()!!.toLowerCase().isEmpty()) {
                    edtPropertyAddress.setText(placeData.getName())
                    edtPropertyAddress.setSelection(edtPropertyAddress.getText().length)
                } else edtPropertyAddress.redError()
                for (i in 0 until placeData.getAddressComponents()!!.asList().size) {
                    val place: AddressComponent =
                        placeData.getAddressComponents()!!.asList().get(i)


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
                    } /*else if (place.types.contains("locality")&& place.types.contains("political")) {
                            edtCity.apply {
                                hint = ""
                                setText(place.name)
                            }
                        }*/


//                    if (place.types.contains("street_number")) {
//                        edtAddress.setText(place.name)
//                        edtAddress.setSelection(edtAddress.getText().length)
//                    }
//                    if ( /*place.getTypes().contains("neighborhood")||*/place.types
//                            .contains("route")
//                    ) {
//                        edtAddress.setText("${edtAddress.getText()} ${place.name}")
//                    }

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
                        hint =getString(R.string.this_field_req)
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

    override fun onResume() {
        super.onResume()
        refreshTheme()
    }

    fun refreshTheme(){
        edtPropertyAddress.viewTheme()
        edtSquareFeet.viewTheme()
        edtBedRoom.viewTheme()
        edtBathRoom.viewTheme()
        edtLotSize.viewTheme()
        edtCity.viewTheme()
        edtState.viewTheme()
        edtPostCode.viewTheme()
    }


    companion object {
        fun createIntent(
            context: Context,
            forSummary: Boolean = true,
            addinfo: Boolean
        ): Intent {
            return Intent(context, PropertyInfoActivity::class.java).apply {
                putExtra("summary", forSummary)
                putExtra("addinfo", addinfo)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broardReceiver,
            IntentFilter("GO_BACK_SUMMARY")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broardReceiver)
    }

    val broardReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            summary_class=true
            fetchPropertyInfo()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (summary_class==true){
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("GO_BACK_SUMMARY"))
            finish()
        }else{
            finish()
        }
    }
}