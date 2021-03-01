package com.rentify.appcode.dashboard.settings

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.RentifyApp.Companion.session
import com.rentify.database.theme.SessionManager
import com.rentify.util.ColorUtil
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.ThemeHelper
import kotlinx.android.synthetic.main.activity_appearance.*


class AppearanceActivity : AppCompatActivity(), View.OnClickListener {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private var selected_mode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.statusBarTheme()
        setContentView(R.layout.activity_appearance)
        headerTool.headerTheme()
        updateColorView(setUISelectedColor())       // set selected saved theme view
        btnNext.bgTheme()
        btnBack.bgTheme()
        orangeTheme.setOnSingleClickListener(this)
        cbOrangeTheme.setOnClickListener(this)
        redTheme.setOnSingleClickListener(this)
        cbRedTheme.setOnClickListener(this)
        darkPurpleTheme.setOnSingleClickListener(this)
        cbDarkPurpleTheme.setOnClickListener(this)
        blueTheme.setOnSingleClickListener(this)
        cbBlueTheme.setOnClickListener(this)
        greenTheme.setOnSingleClickListener(this)
        cbGreenTheme.setOnClickListener(this)
        darkOrangeTheme.setOnSingleClickListener(this)
        cbDarkOrangeTheme.setOnClickListener(this)
        selected_mode=session(this).getPrefValue(SessionManager.THEME_MODE)!!
        lightMode.isChecked = selected_mode==ThemeHelper.LIGHT_MODE
        darkMode.isChecked = selected_mode==ThemeHelper.DARK_MODE
        systemSettingMode.isChecked = selected_mode==ThemeHelper.DEFAULT_MODE

        modeGroup.setOnCheckedChangeListener { radioGroup, i ->
//            selected_mode = when (true) {
//                lightMode.isChecked -> (ThemeHelper.LIGHT_MODE)
//                darkMode.isChecked -> (ThemeHelper.DARK_MODE)
//                systemSettingMode.isChecked -> (ThemeHelper.DEFAULT_MODE)
//                else -> (ThemeHelper.DEFAULT_MODE)
//            }
//            ThemeHelper.applyTheme(selected_mode)
        }

        btnNext.setOnSingleClickListener(this)
        btnBack.setOnSingleClickListener(this)
    }


    override fun onClick(p0: View) {
        when (p0) {
            cbOrangeTheme -> orangeTheme.performClick()
            cbRedTheme -> redTheme.performClick()
            cbDarkPurpleTheme -> darkPurpleTheme.performClick()
            cbBlueTheme -> blueTheme.performClick()
            cbGreenTheme -> greenTheme.performClick()
            cbDarkOrangeTheme -> darkOrangeTheme.performClick()
            orangeTheme, redTheme, darkPurpleTheme, blueTheme, greenTheme, darkOrangeTheme -> {
                updateColorView(p0)
            }
            btnNext -> {
                if (RentifyApp.selectedColor.equals(session(this).getPrefValue(SessionManager.THEME_COLOR))
                        .not()
                )
                    session(this).savePrefValue(
                        SessionManager.THEME_COLOR, RentifyApp.selectedColor?:"#0001FE"
                    )
                if (selected_mode.equals(session(this).getPrefValue(SessionManager.THEME_MODE))
                        .not()
                )
                    session(this).savePrefValue(SessionManager.THEME_MODE, selected_mode)

                showDoneDialog(msg = getString(R.string.changes_saved)) {
                    onBackPressed()
                }
            }
            btnBack -> onBackPressed()
        }
    }

    override fun onBackPressed() {
        if(session(this).getPrefValue(SessionManager.THEME_COLOR)!!.isEmpty().not()) RentifyApp.selectedColor = session(this).getPrefValue(SessionManager.THEME_COLOR)!!
        setUISelectedColor()
        if (selected_mode.equals(session(this).getPrefValue(SessionManager.THEME_MODE)).not())
            ThemeHelper.applyTheme(session(this).getPrefValue(SessionManager.THEME_MODE)!!)
        super.onBackPressed()
    }

    private fun updateColorView(view: View) {
        cbOrangeTheme.isChecked = view == orangeTheme
        cbRedTheme.isChecked = view == redTheme
        cbDarkPurpleTheme.isChecked = view == darkPurpleTheme
        cbBlueTheme.isChecked = view == blueTheme
        cbGreenTheme.isChecked = view == greenTheme
        cbDarkOrangeTheme.isChecked = view == darkOrangeTheme

        RentifyApp.selectedColor = color(view)
        edtTitle.setTextColor(Color.parseColor(RentifyApp.selectedColor))
        (edtAmount.background as GradientDrawable).setStroke(
            3,
            Color.parseColor(RentifyApp.selectedColor)
        )
    }

    private fun color(view: View): String =
        when (view) {
            orangeTheme -> ColorUtil.ORANGE.color
            redTheme -> ColorUtil.RED.color
            darkPurpleTheme -> ColorUtil.PURPLE.color
            blueTheme -> ColorUtil.BLUE.color
            greenTheme -> ColorUtil.GREEN.color
            darkOrangeTheme -> ColorUtil.DARKORANGE.color
            else -> ColorUtil.BLUE.color
        }


    private fun setUISelectedColor(): View =
        when (RentifyApp.selectedColor) {
            ColorUtil.ORANGE.color -> orangeTheme
            ColorUtil.RED.color -> redTheme
            ColorUtil.PURPLE.color -> darkPurpleTheme
            ColorUtil.BLUE.color -> blueTheme
            ColorUtil.GREEN.color -> greenTheme
            ColorUtil.DARKORANGE.color -> darkOrangeTheme
            else -> blueTheme
        }

}