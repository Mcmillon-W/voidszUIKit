package com.hp.voidszuikit

import android.app.Activity
import android.graphics.drawable.Drawable

object ResourceUtils {
    var context: Activity? = null

    fun getDimensionPixelOffset(id: Int): Int {
        return context?.resources?.getDimensionPixelOffset(id) ?: 0
    }

    fun getDimension(id: Int): Float {
        return context?.resources?.getDimension(id) ?: 0f
    }

    fun getColor(id: Int): Int {
        return context?.resources?.getColor(id) ?: 0
    }

    fun getString(id: Int): String? {
        return  context?.resources?.getString(id)
    }

    fun getDrawable(id: Int): Drawable? {
        return  context?.resources?.getDrawable(id)
    }
}