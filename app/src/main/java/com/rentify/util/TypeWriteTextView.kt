package com.rentify.util

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.TextView

class TypeWriteTextView(context: Context?, attrs: AttributeSet?) : TextView(context, attrs) {
    
    private var textList: List<Char> = emptyList()
    private var currentIndex: Int = 0
    private var delay: Long = 20 // in ms
    private val textHandler = Handler()
    private var listener: (() -> Unit)? = null

    private val characterAdder: Runnable = object : Runnable {
        override fun run() {
            text = textList.subList(0, currentIndex).joinToString("")
            if (currentIndex < textList.size) {
                currentIndex++
                textHandler.postDelayed(this, delay)
            } else {
                listener?.invoke()
            }
        }
    }

    fun animateText(txt: CharSequence, endAnimationListener: (() -> Unit)? = null) {
//        textList = txt.split(Regex("\\s+"))
        textList = txt.toMutableList()
        currentIndex = 0
        listener = endAnimationListener

        textHandler.removeCallbacks(characterAdder)
        textHandler.postDelayed(characterAdder, delay)
    }

    fun setCharacterDelay(m: Long) {
        delay = m
    }
}