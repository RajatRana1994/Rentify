package com.rentify.appcode.dashboard.settings

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.rentify.R
import com.rentify.RentifyApp.Companion.session
import com.rentify.appcode.splash.GetStartedActivity
import com.rentify.database.theme.SessionManager
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.LocaleUtil.Companion.setLocale
import kotlinx.android.synthetic.main.activity_language.*

class LanguageActivity : AppCompatActivity() {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    var currentlang="en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.statusBarTheme()
        setContentView(R.layout.activity_language)
        headerTool.headerTheme()
        englishBtn.isChecked = session(this).getPrefValue(SessionManager.LANGUAGE)!!.isEmpty()|| session(this).getPrefValue(SessionManager.LANGUAGE).equals("en")
        spanishBtn.isChecked = session(this).getPrefValue(SessionManager.LANGUAGE).equals("es")
        currentlang=if (englishBtn.isChecked) "en" else "es"
        btnNext.bgTheme()
        btnBack.bgTheme()
        setPreviewText()
        langGroup.setOnCheckedChangeListener { radioGroup, i ->
            setPreviewText()
        }

        btnBack.setOnClickListener { onBackPressed() }
        btnNext.setOnClickListener {
            setLocale(if (englishBtn.isChecked) "en" else "es")
            session(this).savePrefValue(
                SessionManager.LANGUAGE,
                if (englishBtn.isChecked) "en" else "es"
            )
            showDoneDialog(msg = getString(R.string.changes_saved)) {
                onBackPressed()
            }
        }
    }

    fun setPreviewText(){
        if (englishBtn.isChecked){
            tvPreview.text=getString(R.string.preview)
            edtTitle.text=getString(R.string.purchase_price)
            tvInvest.text=getString(R.string.invest_for_long_term)
        }else{
            tvPreview.text=getString(R.string.preview_es)
            edtTitle.text=getString(R.string.purchase_price_es)
            tvInvest.text=getString(R.string.invest_for_long_term_es)
        }
    }


    override fun onBackPressed() {
        if (currentlang.equals(session(this).getPrefValue(SessionManager.LANGUAGE)))
        super.onBackPressed() else
            startActivity(Intent(this@LanguageActivity,GetStartedActivity::class.java))
    }

}