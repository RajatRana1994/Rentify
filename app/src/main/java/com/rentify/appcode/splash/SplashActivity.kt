package com.rentify.appcode.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.RentifyApp.Companion.inputDataEntity
import com.rentify.RentifyApp.Companion.session
import com.rentify.database.theme.SessionManager
import com.rentify.local.InputDataEntity
import com.rentify.util.CheckPermission
import com.rentify.util.ColorUtil
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.LocaleUtil.Companion.setLocale
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity(), CheckPermission.PermissionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.statusBarTheme()
//        window!!.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_splash)
//        CheckPermission(this, this)
        inputDataEntity= InputDataEntity()
        splashTheme.background = ContextCompat.getDrawable(
            this@SplashActivity, when (session(this).getPrefValue(SessionManager.THEME_COLOR)) {
                ColorUtil.BLUE.color -> R.drawable.ic_splash_bg
                ColorUtil.ORANGE.color -> R.drawable.orange_splash
                ColorUtil.DARKORANGE.color -> R.drawable.orange_splash
                ColorUtil.GREEN.color -> R.drawable.green_splash
                ColorUtil.PURPLE.color -> R.drawable.purple_splash
                ColorUtil.RED.color -> R.drawable.red_splash
                else -> R.drawable.ic_splash_bg
            }
        )


        GlobalScope.launch {
            delay(1500)
            launch {
                if (session(this@SplashActivity).getPrefValue(SessionManager.TERMSAPPLIED).equals("1"))
                    startActivity(Intent(this@SplashActivity, GetStartedActivity::class.java))
                else
                    startActivity(Intent(this@SplashActivity, TermsActivity::class.java))
            }
        }

        setLocale(if (session(this@SplashActivity)
                .getPrefValue(SessionManager.LANGUAGE)!!.equals("en")||session(this@SplashActivity)
                .getPrefValue(SessionManager.LANGUAGE)!!.isEmpty()) "en" else "es")

    }

    override fun onPermissionGranted() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("saaa", "aaaa")
    }


}