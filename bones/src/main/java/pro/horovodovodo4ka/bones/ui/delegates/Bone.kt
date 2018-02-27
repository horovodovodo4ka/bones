package pro.horovodovodo4ka.bones.ui.delegates

import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling

/**
 * Simple delegate that implements bone holder.
 * Semantically used for nested fragments and views.
 */
class Content<T : Bone> : BoneSibling<T> {
    override lateinit var bone: T
}