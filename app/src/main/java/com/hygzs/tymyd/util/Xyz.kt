package com.hygzs.tymyd.util

import android.view.MotionEvent
import android.view.View
import android.widget.EditText

/*
* Created by xyz on 2023/11/27
* 所以你瞅啥？
*/

object Xyz {
    fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationOnScreen(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.rawX > left && event.rawX < right && event.rawY > top && event.rawY < bottom)
        }
        return false
    }
}