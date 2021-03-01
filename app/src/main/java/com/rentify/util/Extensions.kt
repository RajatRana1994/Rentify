package com.rentify.util

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.SystemClock
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rentify.R
import com.rentify.RentifyApp
import java.text.NumberFormat
import java.util.*

open class Extensions {
    companion object {

        const val ON_CLICK_DELAY = 200
        var lastTimeClicked = 0L

        fun TextView.viewTheme() = (background as GradientDrawable).setStroke(
            3,
            Color.parseColor(RentifyApp.selectedColor ?: "#0001FE")
        )

        fun EditText.viewTheme() = (background as GradientDrawable).setStroke(
            3,
            Color.parseColor(RentifyApp.selectedColor ?: "#0001FE")
        )

        fun View.bgTheme() {
            backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(RentifyApp.selectedColor ?: "#0001FE"))
        }

        fun View.headerTheme() {
            background = ContextCompat.getDrawable(
                context, when (RentifyApp.selectedColor) {
                    ColorUtil.BLUE.color -> R.drawable.header_blue
                    ColorUtil.ORANGE.color -> R.drawable.header_orange_dark
                    ColorUtil.DARKORANGE.color -> R.drawable.ic_bg_orange
                    ColorUtil.GREEN.color -> R.drawable.header_green
                    ColorUtil.PURPLE.color -> R.drawable.header_purple
                    ColorUtil.RED.color -> R.drawable.header_red
                    else -> R.drawable.header_blue
                }
            )
        }

        fun String.emptyIfZero(): String {
            try {
                return if (toDouble() > 0) this else ""
            } catch (e: NumberFormatException) {
            }
            return this
        }

        fun String.zeroIfEmpty(): String {
            try {
                return if (this.clearAllFormat().toDouble() > 0) this else "0"
            } catch (e: NumberFormatException) {
            }
            return "0"
        }

        fun Window.statusBarTheme() {
            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                ) {
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    statusBarColor = Color.parseColor(RentifyApp.selectedColor ?: "#0001FE")
                    val darkness =
                        1 - (0.299 * Color.red(
                            Color.parseColor(
                                RentifyApp.selectedColor ?: "#0001FE"
                            )
                        ) + 0.587 * Color.green(
                            Color.parseColor(RentifyApp.selectedColor ?: "#0001FE")
                        ) + 0.114 * Color.blue(
                            Color.parseColor(
                                RentifyApp.selectedColor ?: "#0001FE"
                            )
                        )) / 255;
                    if (darkness < 0.5) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            getDecorView()
                                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                        };
                    }
                }
            } catch (e: Exception) {
            }
        }

        fun EditText.clearFormat(): String = text.toString().replace("[$,.%]", "")

        fun String.clearAllFormat(): String = replace(Regex("[$,.%]"), "")
        fun String.clearFormat(): String {
            return if (this.isEmpty()||this.trim().equals("$")) "0.0" else
                this.replace(Regex("[$,%]"), "")
        }

        fun String.addFormat(): String {
            var v = "0"
            if (isEmpty()) v = "0" else v = this
            val currencyInstance = NumberFormat.getCurrencyInstance(Locale.US);
            currencyInstance.currency = Currency.getInstance("USD")
            return currencyInstance.format(v.toLong()).replace(".00", "")
        }

        fun String.addFormatDecimal(currency: Boolean = true): String {
            var v = "0"
            if (isEmpty()) v = "0" else v = this
            val amount = String.format(Locale.getDefault(), "%.2f", v.toDouble())
            val currencyInstance = NumberFormat.getCurrencyInstance(Locale.US);
            currencyInstance.currency = Currency.getInstance("USD")
            return if (currency) currencyInstance.format(amount.toDouble()) else amount
        }

        fun EditText.addCurrencyFormat(onAction: () -> Unit) {
            filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                if (source.length > 0) {
                    if (!Character.isDigit(source[0])) return@InputFilter "" else {
                        if (dstart == 0) {
                            onAction.invoke()
                            return@InputFilter "$$source"
//                        }
//                        else if (dstart == 2) {
//                            onAction.invoke()
//                            return@InputFilter ",$source"
                        } else if (dstart == 4) {
                            onAction.invoke()
                            return@InputFilter ",$source"
                        } else if (dstart == 8) {
                            onAction.invoke()
                            return@InputFilter ",$source"
                        } else if (dstart >= 25) {
                            onAction.invoke()
                            return@InputFilter ""
                        }
                    }
                }
                null
            })
        }


        fun EditText.addAmountWatcher() = object : TextWatcher {
            var lastText = ""
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (!lastText.equals(s.toString()) && s.toString().contains(".").not()) {
                    if (s.toString().isNotEmpty() && s.length > 3) {
                        removeTextChangedListener(this);
                        val parsed = s.toString().clearFormat()
                        val formatted = parsed.addFormat()
                        setText(formatted);
                        setSelection(formatted.length);
                        addTextChangedListener(this);
                    } else {
                        if (s.toString().equals("$")) {
                            setText("")
                        } else if (s.toString().isNotEmpty()) {
                            removeTextChangedListener(this);
                            val parsed = s.toString().clearFormat()
                            val formatted = "$$parsed"
                            setText(formatted);
                            setSelection(formatted.length);
                        }
                        addTextChangedListener(this);
                    }
                } else if (s.toString().contains(".") && s.toString()
                        .substringAfter(".").length > 2
                ) {
                    val formatted = s.toString().substring(0, s.toString().length - 1)
                    setText(formatted)
                    setSelection(formatted.length);
                }
                lastText = text.toString()
            }
        }

        fun EditText.addAmountWatcher(onAction: () -> Unit) = object : TextWatcher {
            var lastText = ""
            override fun afterTextChanged(p0: Editable?) {
                onAction()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
//                if (s.toString().trim().equals("$")){
//                    setText("")
//                }
//                if (!lastText.equals(s.toString()) && s.toString().contains(".").not()) {
//                    if (s.toString().isNotEmpty() && s.length > 3) {
//                        removeTextChangedListener(this);
//                        val parsed = s.toString().clearFormat()
//                        val formatted = parsed.addFormat()
//                        setText(formatted);
//                        setSelection(formatted.length);
//                        addTextChangedListener(this);
//                    } else {
//                        if (s.toString().equals("$")) {
//                            setText("")
//                        } else if (s.toString().isNotEmpty()) {
//                            removeTextChangedListener(this);
//                            val parsed = s.toString().clearFormat()
//                            val formatted = "$$parsed"
//                            setText(formatted);
//                            setSelection(formatted.length);
//                        }
//                        addTextChangedListener(this);
//                    }
//                } else if (s.toString().contains(".") && s.toString()
//                        .substringAfter(".").length > 2
//                ) {
//                    val formatted = s.toString().substring(0, s.toString().length - 1)
//                    setText(formatted)
//                    setSelection(formatted.length);
//                }
//                lastText = text.toString()
            }
        }


        fun EditText.addPercentWatcher() = object : TextWatcher {
            var lastPer = ""
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (!lastPer.equals(s.toString()) && s.toString().contains(".").not()) {
                    if (s.toString().isNotEmpty()) {
                        removeTextChangedListener(this);
                        var parsed = s.toString().clearFormat()
                        val count = parsed.length
                        if (parsed.isNotEmpty()) {
                            if (parsed.toDouble() > 100) parsed = "100%" else parsed = "$parsed%"
                        } else {
                            parsed = ""
                        }
                        setText(parsed);
                        setSelection(count)
                        addTextChangedListener(this);
                    } else {
                        setText("")
                        addTextChangedListener(this);
                    }
                } else if (s.toString().contains(".")
                ) {
                    val formatted =
                        s.toString().substring(0, s.toString().length - 1).replace(".00", "")
                    if (formatted.clearFormat().toDouble() > 100) {
                        setText("100%")
                        setSelection(3);
                    } else {
                        setText("$formatted%")
                        setSelection(formatted.length);
                    }
                }
                lastPer = text.toString()
            }
        }

        fun EditText.addPercentWatcher(onAction: () -> Unit) = object : TextWatcher {
            var lastPer = ""
            override fun afterTextChanged(p0: Editable?) {
                onAction()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (lastPer.length<s.length) {
                    if (!lastPer.equals(s.toString())) {
                        var str = s.toString()
                        if (str.contains("%")) str = s.toString().replace("%", "")
                        removeTextChangedListener(this)
                        /*if (str.contains(".") && (str.substringAfter(".").length <3)) {
                        val first = str.substringBefore(".")
                        val sec = str.substringAfter(".").replace(".","")
                        str = String.format(Locale.getDefault(), "%.2f", ("$first.$sec").toDouble())
                    }else*/ if (str.contains(".") && (str.substringAfter(".").length > 2)) {
                            val first = str.substringBefore(".")
                            val sec = str.substringAfter(".").replace(".", "")
                            str = String.format(
                                Locale.getDefault(),
                                "%.3f",
                                ("$first.$sec").toDouble()
                            )
                        }/*else if (str.contains(".") && str.substringAfter(".").contains(".")){
                        val first = str.substringBefore(".")
                        val sec = str.substringAfter(".").replace(".","")
                        str = String.format(Locale.getDefault(), "%.1f", ("$first.$sec").toDouble())
                    }*/
                        try {
                            if (str.toDouble() > 100) str = str.takeLast(1)
                            else str =
                                if (str.contains(".") && str.substringAfter(".").length > 1) {
                                    if (str.substringAfter(".").length > 1) str.toDouble()
                                        .toString()
                                    else str.toDouble().toString()
                                } else if (str.contains(".") && str.substringAfter(".").length > 0) String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    str.toDouble()
                                )
                                else str
                        } catch (e: Exception) {
                        }
                        setText("$str%");
                        setSelection(str.length)
                        addTextChangedListener(this)
                    }
//                if (!lastPer.equals(s.toString()) && s.toString().contains(".").not()) {
//                    if (s.toString().isNotEmpty()) {
//                        removeTextChangedListener(this);
//                        var parsed = s.toString().clearFormat()
//                        val count = parsed.length
//                        if (parsed.isNotEmpty()) {
//                            if (parsed.toDouble() > 100) parsed = "100%" else parsed = "$parsed%"
//                        } else {
//                            parsed = ""
//                        }
//                        setText(parsed);
//                        setSelection(count)
//                        addTextChangedListener(this);
//                    } else {
//                        setText("")
//                        addTextChangedListener(this);
//                    }
//                } else if (s.toString().contains(".")) {
//                    if ((s.toString().equals(".%")||s.toString().equals(".")||s.toString().contains("0."))&&!lastPer.equals(s.toString())) {
//                        lastPer = "${s.toString().clearFormat()}%"
//                        setText("${s.toString().clearFormat()}%")
//                        setSelection(this@addPercentWatcher.length())
//                    } else{
//                        val formatted =
//                            s.toString().substring(0, s.toString().length - 1).replace(".00", "")
//                        if (formatted.clearFormat().toDouble() > 100) {
//                            setText("100%")
//                            setSelection(3);
//                        } else {
//                            setText("$formatted%")
//                            setSelection(formatted.length);
//                        }
//                    }
//                }
                }
                lastPer = text.toString()
            }
        }

        fun View.setOnSingleClickListener(onSafeClick: (View) -> Unit) {
            setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastTimeClicked < ON_CLICK_DELAY) {
                    return@setOnClickListener
                }
                lastTimeClicked = SystemClock.elapsedRealtime()
                onSafeClick(this)
            }
        }

        fun View.setOnSingleClickListener(onSafeClick: View.OnClickListener) {
            setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastTimeClicked < ON_CLICK_DELAY) {
                    return@setOnClickListener
                }
                lastTimeClicked = SystemClock.elapsedRealtime()
                onSafeClick.onClick(this)
            }
        }

        fun EditText.addPhoneFormat() {
            filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                if (source.length > 0) {
                    if (!Character.isDigit(source[0])) return@InputFilter "" else {
                        if (dstart == 0) {
                            return@InputFilter "($source"
                        } else if (dstart == 3) {
                            return@InputFilter "$source) "
                        } else if (dstart == 9) return@InputFilter "-$source"
                        else if (dstart >= 14) return@InputFilter ""
                    }
                }
                null
            })
        }

        fun EditText.redError() {
            GradientDrawable().apply {
                cornerRadius = 10f
                setStroke(2, Color.RED)
                setBackgroundDrawable(this)
            }
        }


        public fun AppCompatActivity.showDoneDialog(
            msg: String,
            btnOk: String = this.getString(R.string.ok),
            onSafeClick: () -> Unit
        ) {
            val builder = AlertDialog.Builder(this)
            val viewDialog = this.layoutInflater.inflate(R.layout.dialog_generate_report, null)
            builder.setView(viewDialog)
            val alertDialog = builder.create()

            if (btnOk.equals(this.getString(R.string.ok))) viewDialog.findViewById<TextView>(R.id.btnConfirm).visibility =
                View.GONE
            if (btnOk.equals(this.getString(R.string.ok))) viewDialog.findViewById<View>(R.id.view).visibility =
                View.GONE

            viewDialog.findViewById<TextView>(R.id.tvKeyStatus).text = msg

            viewDialog.findViewById<TextView>(R.id.btnCancel).apply {
                text = btnOk
                setOnClickListener {
                    alertDialog.cancel()
                    onSafeClick()
                }
            }
            viewDialog.findViewById<TextView>(R.id.btnConfirm).setOnClickListener {
                alertDialog.cancel()
            }
            alertDialog.setOnDismissListener {
                onSafeClick()
            }
            alertDialog.setCancelable(true)
            alertDialog.show()

            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val displayMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val displayWidth = displayMetrics.widthPixels
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alertDialog.window?.getAttributes())
            val dialogWindowWidth = (displayWidth * 0.8f).toInt()
            layoutParams.width = dialogWindowWidth
            alertDialog.window?.attributes = layoutParams
        }


        public fun AppCompatActivity.showYesNoDialog(msg: String, onSafeClick: () -> Unit) {
            val builder = AlertDialog.Builder(this)
            val viewDialog = this.layoutInflater.inflate(R.layout.dialog_generate_report, null)
            builder.setView(viewDialog)
            val alertDialog = builder.create()

            viewDialog.findViewById<TextView>(R.id.tvKeyStatus).text = msg

            viewDialog.findViewById<TextView>(R.id.btnCancel).apply {
                text = "No"
                setOnClickListener {
                    alertDialog.cancel()
                }
            }
            viewDialog.findViewById<TextView>(R.id.btnConfirm).apply {
                text = "Yes"
                setOnClickListener {
                    alertDialog.cancel()
                    onSafeClick()
                }
            }

            alertDialog.setCancelable(true)
            alertDialog.show()

            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val displayMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val displayWidth = displayMetrics.widthPixels
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alertDialog.window?.getAttributes())
            val dialogWindowWidth = (displayWidth * 0.8f).toInt()
            layoutParams.width = dialogWindowWidth
            alertDialog.window?.attributes = layoutParams
        }

        public fun AppCompatActivity.showCommonDialog(
            msg: String,
            btnYes: String,
            btnNo: String,
            onSafeClick: () -> Unit
        ) {
            val builder = AlertDialog.Builder(this)
            val viewDialog = this.layoutInflater.inflate(R.layout.dialog_generate_report, null)
            builder.setView(viewDialog)
            val alertDialog = builder.create()

            viewDialog.findViewById<TextView>(R.id.tvKeyStatus).text = msg

            viewDialog.findViewById<TextView>(R.id.btnCancel).apply {
                text = btnNo
                setOnClickListener {
                    alertDialog.cancel()
                }
            }
            viewDialog.findViewById<TextView>(R.id.btnConfirm).apply {
                text = btnYes
                setOnClickListener {
                    alertDialog.cancel()
                    onSafeClick()
                }
            }

            alertDialog.setCancelable(true)
            alertDialog.show()

            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val displayMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val displayWidth = displayMetrics.widthPixels
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alertDialog.window?.getAttributes())
            val dialogWindowWidth = (displayWidth * 0.8f).toInt()
            layoutParams.width = dialogWindowWidth
            alertDialog.window?.attributes = layoutParams
        }
    }
}

fun View.takeScreenshotOfView(height: Int, width: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val bgDrawable = background
    if (bgDrawable != null) {
        bgDrawable.draw(canvas)
    } else {
        canvas.drawColor(Color.WHITE)
    }
    draw(canvas)
    return bitmap
}
