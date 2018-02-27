package pro.horovodovodo4ka.bones.ui

import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling

/**
 * Default interface for fragments with support of back button press.
 */
interface FragmentSibling<T : Bone> : BoneSibling<T> {
    /**
     * @return **false** if processed backPress
     */
    fun processBackPress(): Boolean = true
}
