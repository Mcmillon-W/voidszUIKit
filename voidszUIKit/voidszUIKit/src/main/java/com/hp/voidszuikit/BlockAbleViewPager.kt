package com.hp.voidszuikit

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager

class BlockAbleViewPager(context: Context, attrs: AttributeSet?) :
    ViewPager(context, attrs) {
    
    var enabled1: Boolean = true
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (enabled1) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (enabled1) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.enabled1 = enabled
    }
}