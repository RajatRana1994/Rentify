package com.rentify.appcode.splash

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.RentifyApp.Companion.session
import com.rentify.appcode.MainActivity
import com.rentify.appcode.dashboard.DashboardActivity
import com.rentify.appcode.dashboard.userinfo.UserInfoActivity
import com.rentify.appcode.input.InputActivity
import com.rentify.database.theme.SessionManager
import com.rentify.local.InputDataEntity
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.Extensions.Companion.viewTheme
import kotlinx.android.synthetic.main.activity_get_started.*

class GetStartedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)


        btnNovice.setOnClickListener { Intent(this, MainActivity::class.java).apply { startActivity(this) } }
        btnExpert.setOnClickListener {
            RentifyApp.inputDataEntity = InputDataEntity()
            Intent(this, InputActivity::class.java).apply {
            putExtra("clear_extra",true)
            startActivity(this) } }


        ivSetting.setOnClickListener {
            if (intent.hasExtra("pdf_data")){
                showDoneDialog(msg =getString(R.string.cant_open_in_edit_mode)) {}
            }else
            Intent(
                this,
                DashboardActivity::class.java
            ).apply { startActivity(this) }
        }
        if (session(this).getPrefValue(SessionManager.WORKINGMODE).equals("1")){
            btnNovice.performClick()
        }else if (session(this).getPrefValue(SessionManager.WORKINGMODE).equals("2")){
            btnExpert.performClick()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTheme()
        if (session(this).getPrefValue(SessionManager.USER_NAME_USERINFO)!!.isEmpty()){
            startActivity(UserInfoActivity.createIntent(this,false))
            finish()
        }
    }

    fun refreshTheme() {
        if (!session(this).getPrefValue(SessionManager.THEME_COLOR)!!.isEmpty())
            RentifyApp.selectedColor = session(this).getPrefValue(SessionManager.THEME_COLOR)!!
        //paint stroke according to theme color
        ((ContextCompat.getDrawable(this, R.drawable.edt_rounded_theme_drawable)) as GradientDrawable).setStroke(3, Color.parseColor(RentifyApp.selectedColor))

        btnNovice.apply {
            GradientDrawable().apply {
                cornerRadius = 16f
                setStroke(3,  Color.parseColor(RentifyApp.selectedColor))
                setBackgroundDrawable(this)
            }
        }
        btnExpert.apply {
            GradientDrawable().apply {
                cornerRadius = 16f
                setStroke(3, Color.parseColor(RentifyApp.selectedColor))
                setBackgroundDrawable(this)
            }
        }
        window!!.statusBarTheme()
        headerTool.headerTheme()

    }
}