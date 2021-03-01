package com.rentify.appcode.dashboard

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.appcode.dashboard.files.FilesActivity
import com.rentify.appcode.dashboard.photos.PhotoActivity
import com.rentify.appcode.dashboard.propertyinfo.PropertyInfoActivity
import com.rentify.appcode.dashboard.settings.SettingsActivity
import com.rentify.appcode.dashboard.userinfo.UserInfoActivity
import com.rentify.appcode.splash.GetStartedActivity
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.statusBarTheme
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        btnPhotos.setOnSingleClickListener(this)
        btnPropertyinfo.setOnSingleClickListener(this)
        btnUserInfo.setOnSingleClickListener(this)
        btnSettings.setOnSingleClickListener(this)
        btnFiles.setOnSingleClickListener(this)
        btnNewEntry.setOnSingleClickListener(this)
        btnBack.setOnSingleClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0) {
            btnPhotos -> {
                startActivity(Intent(this, PhotoActivity::class.java).putExtra("page","dash"))
            }
            btnPropertyinfo -> {
                startActivity(Intent(this, PropertyInfoActivity::class.java).putExtra("page","dash"))
            }
            btnUserInfo -> {
                startActivity(Intent(this, UserInfoActivity::class.java))
            }
            btnSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            btnFiles -> {
                startActivity(Intent(this, FilesActivity::class.java))
            }
            btnNewEntry -> {
                startActivity(Intent(this, GetStartedActivity::class.java))
            }
            btnBack -> {
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTheme()
    }

    fun refreshTheme(){
        window!!.statusBarTheme()
        headerTool.headerTheme()
        btnBack.bgTheme()

        ivPhoto.setColorFilter(Color.parseColor(RentifyApp.selectedColor))
        ivPropertyinfo.setColorFilter(Color.parseColor(RentifyApp.selectedColor))
        ivUserInfo.setColorFilter(Color.parseColor(RentifyApp.selectedColor))
        ivSettings.setColorFilter(Color.parseColor(RentifyApp.selectedColor))
        ivFiles.setColorFilter(Color.parseColor(RentifyApp.selectedColor))
        ivNewEntry.setColorFilter(Color.parseColor(RentifyApp.selectedColor))
    }
}