package pro.horovodovodo4ka.bones.ui.extensions

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.fragment.app.Fragment

/**
 * Utility helper to capture screenshot of fragment view and placing it as background.
 * Problem is when fragment is removing by animation it's nested fragment are gone with their contents, so its looks like empty screen! :-(
 */
fun Fragment.freezeSnapshotAsBackground() {
    view?.also {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            it.background = BitmapDrawable(Resources.getSystem(), it.takeScreenshot())
        }
    }
}
