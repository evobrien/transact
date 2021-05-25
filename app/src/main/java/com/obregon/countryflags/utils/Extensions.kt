package com.obregon.countryflags.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import timber.log.Timber
import java.io.FileOutputStream
import java.io.IOException


fun Bitmap.saveAs(path: String): Boolean {
    try {
        FileOutputStream(path).use { out ->
            this.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            )
        }
        return true
    } catch (e: IOException) {
        Timber.e(e, "failed to save Image to $path")
    }
    return false
}

inline fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}