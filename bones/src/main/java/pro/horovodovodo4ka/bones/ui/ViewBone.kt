package pro.horovodovodo4ka.bones.ui

import android.view.View
import pro.horovodovodo4ka.bones.Bone

/**
 * Simple realization for use with custom views. Allows easily sync bones on adding as descendants.
 */
abstract class ViewBone : Bone(persistSibling = true) {
    private val siblingId: Int
        get() = (sibling as View).id

    override fun equals(other: Any?): Boolean {
        other as? ViewBone ?: return false
        return siblingId == other.siblingId
    }
}