package com.rentify.appcode.dashboard.settings

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.appcode.dashboard.support.SupportActivity
import com.rentify.database.theme.SessionManager
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.clearFormat
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.showCommonDialog
import com.rentify.util.Extensions.Companion.showYesNoDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.*


class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        btnBack.setOnSingleClickListener { onBackPressed() }
        tvAppearance.setOnSingleClickListener(this)
        tvLanguage.setOnSingleClickListener(this)
        tvWorkingMode.setOnSingleClickListener(this)
        tvDefaultSettings.setOnSingleClickListener(this)
        tvNeedHelp.setOnSingleClickListener(this)
        tvSafetyQues.setOnSingleClickListener(this)
        tvPrivacyQues.setOnSingleClickListener(this)
        tvSpotBug.setOnSingleClickListener(this)
        tvHaveSuggestion.setOnSingleClickListener(this)
        tvResetSettings.setOnSingleClickListener(this)
    }

    override fun onClick(p0: View) {
        val aniSlide: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom)
        p0.startAnimation(aniSlide)

        GlobalScope.launch {
            delay(800)
            launch(Dispatchers.Main) {
                when (p0) {
                    tvAppearance -> {
                        startActivity(Intent(this@SettingsActivity, AppearanceActivity::class.java))
                    }
                    tvLanguage -> {
                        startActivity(Intent(this@SettingsActivity, LanguageActivity::class.java))
                    }
                    tvResetSettings -> {
                        showCommonDialog(getString(R.string.update_defaultSetting),"Ok","Cancel"){
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.RENTAL_INCREASES,"3.00%")
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.APPRECIATION_GROWTH,"3.00%")
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.PRINCIPAL_PAYMENT,"25.00%")
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.IMPOUND_ACCOUNT,"0.33%")
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.VACANCY_FACTOR,"0.00%")
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.LOAN_INTREST,"0.00%")
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.PROPERTY_TAX,"1.125%")
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.HOMEOWNERS_INSURANCE,"0.25%")
                            RentifyApp.session(this@SettingsActivity).savePrefValue(SessionManager.MAINTENANCE_EXP,"0.44%")
                        }
                    }
                    tvWorkingMode -> {
                        startActivity(
                            Intent(
                                this@SettingsActivity,
                                WorkingModeActivity::class.java
                            )
                        )
                    }
                    tvDefaultSettings -> {
                        startActivity(
                            Intent(
                                this@SettingsActivity,
                                DefaultSettingActivity::class.java
                            )
                        )
                    }
                    tvNeedHelp -> {
                        SupportActivity.support_type = SupportActivity.SUPPORT.NEED_HELP
                        startActivity(Intent(this@SettingsActivity, SupportActivity::class.java))
                    }
                    tvSafetyQues -> {
                        SupportActivity.support_type = SupportActivity.SUPPORT.SAFETY_QUES
                        startActivity(Intent(this@SettingsActivity, SupportActivity::class.java))
                    }
                    tvPrivacyQues -> {
                        SupportActivity.support_type = SupportActivity.SUPPORT.PRIVACY_QUES
                        startActivity(Intent(this@SettingsActivity, SupportActivity::class.java))
                    }
                    tvSpotBug -> {
                        SupportActivity.support_type = SupportActivity.SUPPORT.SPOTTED_BUG
                        startActivity(Intent(this@SettingsActivity, SupportActivity::class.java))
                    }
                    tvHaveSuggestion -> {
                        SupportActivity.support_type = SupportActivity.SUPPORT.HAVE_SUGGESTION
                        startActivity(Intent(this@SettingsActivity, SupportActivity::class.java))
                    }
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTheme()
    }

    fun refreshTheme() {
        window!!.statusBarTheme()
        headerTool.headerTheme()
        btnNext.bgTheme()
        btnBack.bgTheme()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}