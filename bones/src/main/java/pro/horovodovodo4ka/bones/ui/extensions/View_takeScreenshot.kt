package pro.horovodovodo4ka.bones.ui.extensions

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.view.View

/**
 * Shorthand method for capturing screenshot from view
 */
fun View.takeScreenshot(): Bitmap? {
    if (width == 0 || height == 0) return null
    return Bitmap.createBitmap(width, height, Config.ARGB_8888)
        .also {
            layout(0, 0, width, height)
            draw(Canvas(it))
        }
}

