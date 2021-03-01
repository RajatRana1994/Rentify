package com.rentify.appcode.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.rentify.R
import com.rentify.RentifyApp.Companion.session
import com.rentify.database.theme.SessionManager
import com.rentify.util.CheckPermission
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_terms.*

class TermsActivity : AppCompatActivity(), CheckPermission.PermissionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window!!.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_terms)
        CheckPermission(this, this)

        isAgreeTerms.setOnClickListener { isAgreeTerms.isChecked = !isAgreeTerms.isChecked }

        btnGetStart.setOnSingleClickListener{
            if (isAgreeTerms.isChecked) {
                session(this).savePrefValue(SessionManager.TERMSAPPLIED, "1")
                startActivity(Intent(this@TermsActivity, GetStartedActivity::class.java))
            }
        }
    }

    override fun onPermissionGranted() {

    }
}