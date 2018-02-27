package pro.horovodovodo4ka.bones.ui.extensions

import android.support.v4.app.Fragment

/**
 * Utility function to add custom behavior on back press for dialog
 */
fun Fragment.interceptBackPress(block: () -> Unit) {
    view?.apply {
        isFocusableInTouchMode = true
        requestFocus()
        setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                android.view.KeyEvent.KEYCODE_BACK -> {
                    block()
                    true
                }
                else -> false
            }
        }
    }
}