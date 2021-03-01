package com.rentify.appcode.dashboard.settings

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.rentify.R
import com.rentify.RentifyApp.Companion.session
import com.rentify.database.theme.SessionManager
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import kotlinx.android.synthetic.main.activity_working_mode.*

class WorkingModeActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_working_mode)
        headerTool.headerTheme()
        btnNext.bgTheme()
        btnBack.bgTheme()

        noviceBtn.isChecked=session(this).getPrefValue(SessionManager.WORKINGMODE).equals("1")
        expertBtn.isChecked=session(this).getPrefValue(SessionManager.WORKINGMODE).equals("2")

        btnBack.setOnSingleClickListener{onBackPressed()}
        btnNext.setOnSingleClickListener{
            session(this).savePrefValue(SessionManager.WORKINGMODE,if(noviceBtn.isChecked) "1" else if(expertBtn.isChecked) "2" else "0")
            showDoneDialog(msg = getString(R.string.changes_saved)) {
                onBackPressed()
            }
        }
        noviceBtn.setOnCheckedChangeListener { compoundButton, b ->
            if (b) expertBtn.isChecked=false
            if (b) tvEntry.animateText(getString(R.string.this_is_example_text))
            tvEntry.visibility=View.VISIBLE
            edtAmountAnimate.visibility=View.VISIBLE
            edtAmount.visibility=View.GONE
            edtTitle.visibility=View.GONE
        }
        expertBtn.setOnCheckedChangeListener { compoundButton, b ->
            if (b) noviceBtn.isChecked=false
            tvEntry.animateText("")
            tvEntry.visibility=View.GONE
            edtAmountAnimate.visibility=View.GONE
            edtAmount.visibility=View.VISIBLE
            edtTitle.visibility=View.VISIBLE
        }
    }
}