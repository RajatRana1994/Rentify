package com.rentify.appcode.dashboard.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rentify.R
import com.rentify.RentifyApp.Companion.session
import com.rentify.database.theme.SessionManager
import com.rentify.local.DefaultSettingsModel
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.clearFormat
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.showYesNoDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.Extensions.Companion.viewTheme
import kotlinx.android.synthetic.main.activity_default_setting.*

class DefaultSettingActivity : AppCompatActivity() {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
    val settingsList by lazy { mutableListOf<DefaultSettingsModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.statusBarTheme()
        setContentView(R.layout.activity_default_setting)
        headerTool.headerTheme()
        btnNext.bgTheme()
        btnBack.bgTheme()
        addSettingsValues()
        btnBack.setOnClickListener { onBackPressed() }
        btnNext.setOnClickListener {
            showDoneDialog(msg = getString(R.string.changes_saved)) {
                onBackPressed()
            }
        }
        settingsList.forEach {
            addAndSaveSetting(it)
        }
    }

    private fun addSettingsValues() {
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.annual_rental_increase_)}(%)",
                if (session(this).getPrefValue(SessionManager.RENTAL_INCREASES)!!
                        .equals("0%") || session(this).getPrefValue(SessionManager.RENTAL_INCREASES)!!
                        .equals("0")
                ) "3.00%" else "${session(this).getPrefValue(SessionManager.RENTAL_INCREASES)!!}%",
                SessionManager.RENTAL_INCREASES
            )
        )
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.area_appreciation_growth)}(%)",
                if (session(this).getPrefValue(SessionManager.APPRECIATION_GROWTH)!!
                        .equals("0%") || session(this).getPrefValue(SessionManager.APPRECIATION_GROWTH)!!
                        .equals("0")
                ) "3.00%" else "${session(this).getPrefValue(SessionManager.APPRECIATION_GROWTH)!!}%",
                SessionManager.APPRECIATION_GROWTH
            )
        )
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.principal_payment)}(%)",
                if (session(this).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)!!
                        .equals("0%") || session(this).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)!!
                        .equals("0")
                ) "25.00%" else "${session(this).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)!!}%",
                SessionManager.PRINCIPAL_PAYMENT
            )
        )
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.impound_account)}(%)",
                if (session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)!!
                        .equals("0%") || session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)!!
                        .equals("0")
                )
                    "0.33%" else "${session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)!!}%",
                SessionManager.IMPOUND_ACCOUNT
            )
        )
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.vacancy_factor_)}",
                if (session(this).getPrefValue(SessionManager.VACANCY_FACTOR)!!
                        .equals("0%")||session(this).getPrefValue(SessionManager.VACANCY_FACTOR)!!.equals("0")
                ) "0.00%" else "${session(this).getPrefValue(SessionManager.VACANCY_FACTOR)!!}%",
                SessionManager.VACANCY_FACTOR
            )
        )
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.loan_intrest)}(%)",
                if (session(this).getPrefValue(SessionManager.LOAN_INTREST)!!
                        .equals("0%")||session(this).getPrefValue(SessionManager.LOAN_INTREST)!!.equals("0")
                ) "0.00%" else "${session(this).getPrefValue(SessionManager.LOAN_INTREST)!!}%",
                SessionManager.LOAN_INTREST
            )
        )
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.property_tax)}(%)",
                if (session(this).getPrefValue(SessionManager.PROPERTY_TAX)!!
                        .equals("0%")||session(this).getPrefValue(SessionManager.PROPERTY_TAX)!!.equals("0")
                ) "1.125%" else "${session(this).getPrefValue(SessionManager.PROPERTY_TAX)!!}%",
                SessionManager.PROPERTY_TAX
            )
        )
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.homeowners_insurance)}(%)",
                if (session(this).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)!!
                        .equals("0%")||session(this).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)!!.equals("0")
                ) "0.25%" else "${session(this).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)!!}%",
                SessionManager.HOMEOWNERS_INSURANCE
            )
        )
        settingsList.add(
            DefaultSettingsModel(
                "${getString(R.string.maintenance_exp)}(%)",
                if (session(this).getPrefValue(SessionManager.MAINTENANCE_EXP)!!
                        .equals("0%")||session(this).getPrefValue(SessionManager.MAINTENANCE_EXP)!!.equals("0")
                ) "0.44%" else "${session(this).getPrefValue(SessionManager.MAINTENANCE_EXP)!!}%",
                SessionManager.MAINTENANCE_EXP
            )
        )
    }


    private fun addAndSaveSetting(input: DefaultSettingsModel) {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.item_input_edit_save_settings, llInflater, false)
        view.findViewById<TextView>(R.id.edtTitle).text = input.title.replace("(%)(%)","(%)")
        view.findViewById<ImageView>(R.id.ivSave).setOnSingleClickListener {
            showYesNoDialog(getString(R.string.update_defaultSetting)) {
                session(this).savePrefValue(
                    input.sessionType,
                    view.findViewById<EditText>(R.id.edtAmount).text.toString().clearFormat()
                        .replace("%%", "")
                )
            }
        }
        view.findViewById<EditText>(R.id.edtAmount).apply {
            viewTheme()
            setText(input.percent .replace("%%", "%"))
        }
        llInflater.addView(view)

    }
}