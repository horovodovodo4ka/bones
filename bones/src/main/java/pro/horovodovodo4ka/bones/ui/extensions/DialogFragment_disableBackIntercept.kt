package pro.horovodovodo4ka.bones.ui.extensions

import android.support.v4.app.DialogFragment
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.extensions.dismiss

/**
 * Adds special keyListener to intercept back press on DialogFragment siblings. Causes simple dismissing.
 */
fun <T> T.disableBackIntercept() where T : DialogFragment, T : BoneSibling<*> {
    isCancelable = false
    dialog.setOnKeyListener { _, keyCode, _ ->
        when (keyCode) {
            android.view.KeyEvent.KEYCODE_BACK -> {
                bone.dismiss()
                true
            }
            else -> false
        }
    }
}