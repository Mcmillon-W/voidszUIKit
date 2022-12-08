package com.hp.voidszuikit

import android.app.Activity
import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get

inline fun <T : Activity?> T?.runSafely(crossinline block: Activity.() -> Unit) {
    this?.takeIf { !this.isFinishing and !this.isDestroyed }?.run { block() }
}

fun showToast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    ResourceUtils.context?.runSafely {
        Toast.makeText(this, text, duration).show()
    }
}

fun <T> List<T>?.getSafely(index: Int): T? {
    return if (this.isSafeIndex(index)) this?.get(index) else null
}

fun ViewGroup?.getSafely(index: Int): View? {
    return if (this.isSafeIndex(index)) this?.get(index) else null
}

fun ViewGroup?.isSafeIndex(index: Int): Boolean {
    return if (this?.childCount ?: 0 > 0) {
        return index >= 0 && index < this?.childCount ?: 0
    } else false
}

fun <T> List<T>?.isSafeIndex(index: Int): Boolean {
    return if (this.isNotNullOrEmpty()) {
        return index >= 0 && index < this?.size ?: 0
    } else false
}

fun <T> T?.isNotNull(): Boolean {
    return this != null
}

fun <T> T?.isNotNullOrBlank(): Boolean {
    return this.isNotNull() && this != ""
}

fun <T> List<T>?.isNotNullOrEmpty(): Boolean {
    return this.isNotNull() && this?.size != 0
}

fun playSound() {
    try {
        ResourceUtils.context?.runSafely {
            MediaPlayer.create(ResourceUtils.context, R.raw.beep).start()
        }
    } catch (e: Exception) {
    }
}
