package com.rentify.appcode.dashboard.support

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rentify.R
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.statusBarTheme
import kotlinx.android.synthetic.main.activity_support.*

class SupportActivity : AppCompatActivity() {
    companion object {
        var support_type: SUPPORT = SUPPORT.NEED_HELP
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
    var prefixStr = getString(R.string.i_need_help_with_the)

    enum class SUPPORT { NEED_HELP, SAFETY_QUES, PRIVACY_QUES, SPOTTED_BUG, HAVE_SUGGESTION }

    val supportList by lazy { mutableListOf<String>() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.statusBarTheme()
        setContentView(R.layout.activity_support)
        headerTool.headerTheme()
        addSupportContent()
        btnBack.bgTheme()
        setPrefix()
        btnBack.setOnClickListener { onBackPressed() }
    }

    private fun setPrefix() {
        return when (support_type) {
            SUPPORT.NEED_HELP -> prefixStr = getString(R.string.i_need_help_with_the)
            SUPPORT.SAFETY_QUES -> prefixStr = getString(R.string.i_have_safety_ques_on_the)
            SUPPORT.PRIVACY_QUES -> prefixStr = getString(R.string.i_have_privacy_ques_on_the)
            SUPPORT.SPOTTED_BUG -> prefixStr = getString(R.string.i_spotted_bug_on_the)
            SUPPORT.HAVE_SUGGESTION -> prefixStr = getString(R.string.i_have_suggestion_on_the)
        }
    }

    private fun addSupportContent() {
        supportList.add(getString(R.string.novice_page_1_18))
        supportList.add(getString(R.string.income_exp_page))
        supportList.add(getString(R.string.graphic_pages_1_7))
        supportList.add(getString(R.string.client_summary_report))
        supportList.add(getString(R.string.dashboard_photos))
        supportList.add(getString(R.string.dashboard_property_info))
        supportList.add(getString(R.string.dashboard_user_info))
        supportList.add(getString(R.string.dashboard_files))
        supportList.add(getString(R.string.dashboard_new_entry))
        supportList.add(getString(R.string.settings_appearance))
        supportList.add(getString(R.string.settings_language))
        supportList.add(getString(R.string.settingsworking_mode))
        supportList.add(getString(R.string.settings_default_settings))
        supportList.add(getString(R.string.unlisted_issue))

        supportList.forEach {
            addSupports(it)
        }

    }

    private fun addSupports(type: String) {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.item_support, llInflater, false)
        view.findViewById<TextView>(R.id.titleHeader).apply {
            text = type
            setOnSingleClickListener {
                composeMail(prefixStr + type)
            }
        }
        llInflater.addView(view)
    }

    private fun composeMail(subject: String) {
        var isFound = false
        val shareIntent = Intent(Intent.ACTION_SEND);
        try {
            shareIntent.type = "*/*";
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent by Android device")
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
            val pm = packageManager;
            val activityList = pm.queryIntentActivities(shareIntent, 0);
            for (app in activityList) {
                if ((app.activityInfo.name).contains("google.android.gm") || (app.activityInfo.name).contains(
                        "gmail"
                    )
                ) {
                    isFound = true
                    val activity = app.activityInfo
                    val name = ComponentName(activity.applicationInfo.packageName, activity.name)
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shareIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    shareIntent.component = name
                    startActivity(shareIntent)
                }
            }
            if (!isFound) {
                try {
                    val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntent(arrayOfNulls<Parcelable>(shareIntent.size())))
                    startActivity(chooserIntent)
                } catch (e: java.lang.Exception) {
                    Toast.makeText(this@SupportActivity, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            try {
                val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
                startActivity(chooserIntent)
            } catch (e: java.lang.Exception) {
                Toast.makeText(this@SupportActivity, e.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}