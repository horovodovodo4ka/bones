package pro.horovodovodo4ka.bones.ui

import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.extensions.processBackPress

/**
 * Default implementation for fragments which are siblings of spine.
 */
interface SpineNavigatorInterface<T : Spine> : ContainerFragmentSibling<T> {
    override fun processBackPress(): Boolean {
        if (!bone.skull.processBackPress()) return false

        val oldBone = bone.skull
        bone.dismiss()
        return oldBone == bone.skull
    }
}
